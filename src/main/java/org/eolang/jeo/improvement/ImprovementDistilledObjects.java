/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo.improvement;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.Improvement;
import org.eolang.jeo.Representation;
import org.eolang.jeo.representation.EoRepresentation;
import org.eolang.jeo.representation.HexData;
import org.eolang.jeo.representation.xmir.XmlClass;
import org.eolang.jeo.representation.xmir.XmlField;
import org.eolang.jeo.representation.xmir.XmlInstruction;
import org.eolang.jeo.representation.xmir.XmlMethod;
import org.eolang.jeo.representation.xmir.XmlProgram;
import org.w3c.dom.Node;

/**
 * Distilled objects improvement.
 * You can find the description of the optimization right
 * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/102">here</a>
 * @since 0.1.0
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class ImprovementDistilledObjects implements Improvement {
    @Override
    public Collection<? extends Representation> apply(
        final Collection<? extends Representation> representations
    ) {
        final List<DecoratorPair> decorators = new ArrayList<>(
            ImprovementDistilledObjects
                .decorators(
                    representations.stream()
                        .sorted(Comparator.comparing((Representation ir) -> ir.name()))
                        .collect(Collectors.toList())
                )
        );
        final List<Representation> generated = decorators.stream()
            .map(DecoratorPair::combine)
            .collect(Collectors.toList());
        Logger.info(
            this,
            String.format(
                "Distilled objects improvement is successfully applied. Generated classes: %s, total %d",
                generated.stream().map(Representation::name).collect(Collectors.toList()),
                generated.size()
            )
        );
        return Stream.concat(
            generated.stream(),
            representations.stream()
                .map(repr -> ImprovementDistilledObjects.replaceConstructors(decorators, repr))
        ).collect(Collectors.toList());
    }

    /**
     * Replace constructor invocations.
     * @param decorators Decorators.
     * @param representation Representation.
     * @return Representation with replaced constructors.
     * @todo #162:90min Refactor replaceConstructors method.
     *  Right now it's a method with high complexity and it's hard to read it.
     *  We need to refactor it or just simplify somehow.
     */
    private static Representation replaceConstructors(
        final List<? extends DecoratorPair> decorators,
        final Representation representation
    ) {
        final XML xmir = representation.toEO();
        final XmlClass clazz = new XmlProgram(xmir).top();
        for (final DecoratorPair decorator : decorators) {
            if (representation.name().equals(decorator.originalDecoratorName())) {
                continue;
            }
            ImprovementDistilledObjects.replace(
                clazz,
                decorator.targetNew(),
                decorator.replacementNew()
            );
            ImprovementDistilledObjects.replace(
                clazz,
                decorator.targetSpecial(),
                decorator.replacementSpecial()
            );
            clazz.methods()
                .stream()
                .map(XmlMethod::instructions)
                .flatMap(Collection::stream)
                .forEach(
                    instruction ->
                        instruction.replaceArguementsValues(
                            decorator.originalDecoratorName(),
                            decorator.combinedName()
                        )
                );
        }
        return new EoRepresentation(new XmlProgram(xmir).with(clazz).toXmir());
    }

    /**
     * Replace instructions.
     * @param clazz Class where to replace.
     * @param target What should be replaced.
     * @param replacement Replacement.
     * @checkstyle ModifiedControlVariableCheck (200 lines)
     * @checkstyle NestedForDepthCheck (200 lines)
     */
    private static void replace(
        final XmlClass clazz,
        final List<XmlInstruction> target,
        final List<XmlInstruction> replacement
    ) {
        for (final XmlMethod method : clazz.methods()) {
            final List<XmlInstruction> instructions = method.instructions();
            final List<XmlInstruction> updated = new ArrayList<>(0);
            final int size = target.size();
            for (int index = 0; index < instructions.size(); ++index) {
                final List<XmlInstruction> stack = new ArrayList<>(0);
                for (
                    int inner = 0;
                    inner < size && index < instructions.size();
                    ++inner
                ) {
                    final XmlInstruction targeted = target.get(inner);
                    final XmlInstruction current = instructions.get(index);
                    if (current.equals(targeted)) {
                        if (inner == size - 1) {
                            updated.addAll(replacement);
                            stack.clear();
                        } else {
                            stack.add(current);
                            ++index;
                        }
                    } else {
                        updated.addAll(stack);
                        updated.add(current);
                        break;
                    }
                }
            }
            method.setInstructions(updated);
        }
    }

    /**
     * Find decorators.
     * @param eobjects EObjects.
     * @return Decorators.
     * @todo #152:90min Implement decorator search.
     *  Right now we just return the first two EObjects as decorators which is not correct.
     *  We need to implement a proper decorator search. When it's done, remove that puzzle.
     */
    private static Collection<DecoratorPair> decorators(
        final List<? extends Representation> eobjects
    ) {
        final Collection<DecoratorPair> result;
        if (eobjects.isEmpty() || eobjects.size() < 2) {
            result = Collections.emptyList();
        } else {
            result = Collections.singleton(new DecoratorPair(eobjects.get(0), eobjects.get(1)));
        }
        return result;
    }

    /**
     * Decorator pair.
     * Pair of XMIR files where the first one is a decorator and
     * the second one is a decorated object.
     * @since 0.1.0
     */
    @SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods"})
    private static class DecoratorPair {

        /**
         * Decorated object.
         */
        private final Representation decorated;

        /**
         * Object that decorates.
         */
        private final Representation decorator;

        /**
         * Constructor.
         * @param decorated Decorated object.
         * @param decorator Object that decorates.
         */
        DecoratorPair(final Representation decorated, final Representation decorator) {
            this.decorated = decorated;
            this.decorator = decorator;
        }

        /**
         * List of NEW instructions that should be replaced.
         * @return List of NEW instructions.
         */
        private List<XmlInstruction> targetNew() {
            final String dup = "<o base='opcode' name='DUP-89-53'/>";
            return Arrays.asList(
                new XmlInstruction(
                    String.join(
                        "",
                        "<o base='opcode' name='NEW-187-50'>",
                        "<o base='string' data='bytes'>",
                        new HexData(this.decorator.name()).value(),
                        "</o>",
                        "</o>"
                    )
                ),
                new XmlInstruction(dup),
                new XmlInstruction(
                    String.join(
                        "",
                        "<o base='opcode' name='NEW-187-50'>",
                        "<o base='string' data='bytes'>",
                        new HexData(this.decorated.name()).value(),
                        "</o>",
                        "</o>"
                    )
                ),
                new XmlInstruction(dup)
            );
        }

        /**
         * Replacement for NEW instruction.
         * @return Replacement.
         */
        private List<XmlInstruction> replacementNew() {
            return Arrays.asList(
                new XmlInstruction(
                    String.join(
                        "",
                        "<o base='opcode' name='NEW-187-50'>",
                        "<o base='string' data='bytes'>",
                        new DecoratorCompositionName(this.decorated, this.decorator).hex(),
                        "</o>",
                        "</o>"
                    )
                ),
                new XmlInstruction("<o base='opcode' name='DUP-89-53'/>")
            );
        }

        /**
         * List of INVOKESPECIAL instructions that should be replaced.
         * @return List of INVOKESPECIAL instructions.
         */
        private List<XmlInstruction> targetSpecial() {
            return Arrays.asList(
                new XmlInstruction(
                    String.join(
                        "",
                        "<o base='opcode' name='INVOKESPECIAL-183-55'>",
                        "<o base='string' data='bytes'>",
                        new HexData(this.decorated.name()).value(),
                        "</o>",
                        "<o base='string' data='bytes'>",
                        new HexData("<init>").value(),
                        "</o>",
                        "<o base='string' data='bytes'>",
                        new HexData("(I)V").value(),
                        "</o>",
                        "</o>"
                    )
                ),
                new XmlInstruction(
                    String.join(
                        "",
                        "<o base='opcode' name='INVOKESPECIAL-183-55'>",
                        "<o base='string' data='bytes'>",
                        new HexData(this.decorator.name()).value(),
                        "</o>",
                        "<o base='string' data='bytes'>",
                        new HexData("<init>").value(),
                        "</o>",
                        "<o base='string' data='bytes'>",
                        new HexData("(Lorg/eolang/jeo/A;)V").value(),
                        "</o>",
                        "</o>"
                    )
                )
            );
        }

        /**
         * Replacement for INVOKESPECIAL instruction.
         * @return Replacement.
         */
        private List<XmlInstruction> replacementSpecial() {
            return Collections.singletonList(
                new XmlInstruction(
                    String.join(
                        "",
                        "<o base='opcode' name='INVOKESPECIAL-183-55'>",
                        "<o base='string' data='bytes'>",
                        new DecoratorCompositionName(this.decorated, this.decorator).hex(),
                        "</o>",
                        "<o base='string' data='bytes'>",
                        new HexData("<init>").value(),
                        "</o>",
                        "<o base='string' data='bytes'>",
                        new HexData("(I)V").value(),
                        "</o>",
                        "</o>"
                    )
                )
            );
        }

        /**
         * Original decorator name.
         * @return Decorator name.
         */
        private String originalDecoratorName() {
            return this.decorator.name();
        }

        /**
         * Combined name.
         * @return Name of combined class.
         */
        private String combinedName() {
            return new DecoratorCompositionName(this.decorated, this.decorator).value();
        }

        /**
         * Combine two representations into one.
         * @return Combined representation.
         */
        private Representation combine() {
            final String name = this.combinedName();
            final XmlProgram program = new XmlProgram(this.decorator.toEO())
                .copy()
                .withName(name)
                .withTime(LocalDateTime.now())
                .withListing("");
            this.handleClass(program.top().withName(name));
            return new EoRepresentation(program.toXmir());
        }

        /**
         * Handle decorator class.
         * @param decor Decorator.
         * @todo #162:90min Refactor handleClass method.
         *  Right now it's a method with high complexity and it's hard to read it.
         *  We need to refactor it or inline into some other method.
         */
        private void handleClass(final XmlClass decor) {
            final Node root = decor.node();
            DecoratorPair.removeOldFields(root);
            DecoratorPair.removeOldConstructors(root);
            final XmlClass clazz = new XmlProgram(this.decorated.toEO()).top();
            clazz.fields().forEach(decor::withField);
            for (final XmlMethod method : clazz.methods()) {
                if (method.isConstructor()) {
                    for (final XmlInstruction instruction : method.instructions()) {
                        instruction.replaceArguementsValues(this.decorated.name(),
                            this.combinedName()
                        );
                    }
                    decor.withConstructor(method);
                } else {
                    this.replaceOldInvokationsWithNewInvocations(decor, method);
                }
            }
        }

        /**
         * Remove old constructors.
         * @param root Root node.
         * @todo #157:90min Handle constructors correctly during inlining optimization.
         *  Right now we just remove all constructors from the decorated object.
         *  It's not correct, because we need to handle constructors correctly.
         */
        private static void removeOldConstructors(final Node root) {
            new XmlClass(root).constructors()
                .stream()
                .map(XmlMethod::node)
                .forEach(root::removeChild);
        }

        /**
         * Remove old fields.
         * @param root Root node.
         * @todo #157:90min Handle fields correctly during inlining optimization.
         *  Right now we just remove all fields from the decorated object.
         *  It's not correct, because we need to handle fields correctly and probably remove only
         *  those fields that are used in the decorator.
         */
        private static void removeOldFields(final Node root) {
            new XmlClass(root).fields()
                .stream()
                .map(XmlField::node)
                .forEach(root::removeChild);
        }

        /**
         * Replace method content.
         * This function scans all the class methods and tries to find all invocations of the old
         * object methods. If it finds any, it replaces them with the new object invocations.
         * For example, if we had the next code:
         * a.foo();
         * it will be replaced with
         * b.foo();
         * and then b.foo() will be inlined.
         * @param where To replace.
         * @param what To inline.
         */
        private void replaceOldInvokationsWithNewInvocations(
            final XmlClass where,
            final XmlMethod what
        ) {
            final String old = this.decorated.name();
            for (final XmlMethod candidate : where.methods()) {
                candidate.inline(what, old, this.combinedName());
            }
        }
    }
}
