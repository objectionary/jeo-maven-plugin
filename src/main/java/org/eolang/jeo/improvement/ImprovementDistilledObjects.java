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
import com.jcabi.xml.XMLDocument;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
import org.objectweb.asm.Opcodes;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Distilled objects improvement.
 * You can find the description of the optimization right
 * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/102">here</a>
 * @since 0.1.0
 * @todo #157:90min ImprovementDistilledObjects is needed to be refactored.
 *  Right now it's a big class with a lot of methods, repetition and high complexity.
 *  We need to refactor it into a set of smaller classes and remove all
 *  linter warnings.
 * @checkstyle NestedIfDepthCheck (500 lines)
 * @checkstyle NestedForDepthCheck (500 lines)
 */
@SuppressWarnings({"PMD.CollapsibleIfStatements", "PMD.AvoidDuplicateLiterals"})
public final class ImprovementDistilledObjects implements Improvement {
    @Override
    public Collection<? extends Representation> apply(
        final Collection<? extends Representation> representations
    ) {
        final List<DecoratorPair> decorators = ImprovementDistilledObjects
            .decorators(
                representations.stream()
                    .sorted(Comparator.comparing((Representation ir) -> ir.name()))
                    .collect(Collectors.toList())
            )
            .stream()
            .collect(Collectors.toList());
        final List<Representation> additional = decorators.stream()
            .map(DecoratorPair::combine)
            .collect(Collectors.toList());
        Logger.info(
            this,
            String.format(
                "Distilled objects improvement is successfully applied. Generated classes: %s, total %d",
                additional.stream().map(Representation::name).collect(Collectors.toList()),
                additional.size()
            )
        );
        return Stream.concat(
            representations.stream()
                .map(repr -> ImprovementDistilledObjects.replaceConstructors(decorators, repr)),
            additional.stream()
        ).collect(Collectors.toList());
    }

    /**
     * Replace constructor invocations.
     * @param decorators Decorators.
     * @param representation Representation.
     * @return Representation with replaced constructors.
     * @todo #161:90min Refactor replaceConstructors method.
     *  Right now it's a big method with a lot of repetition and high complexity.
     *  We have to simplify it and remove all linter warnings.
     * @checkstyle NestedIfDepthCheck (200 lines)
     */
    private static Representation replaceConstructors(
        final List<DecoratorPair> decorators,
        final Representation representation
    ) {
        final XML xmir = representation.toEO();
        final XmlClass clazz = new XmlClass(xmir);
        for (final DecoratorPair decorator : decorators) {
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
            ImprovementDistilledObjects.replaceArguments(
                clazz
            );
        }
        final Node replacement = clazz.node();
        final Node program = xmir.node();
        final NodeList top = program.getChildNodes();
        for (int index = 0; index < top.getLength(); ++index) {
            final Node current = top.item(index);
            if (current.getNodeName().equals("program")) {
                final NodeList subchildren = current.getChildNodes();
                for (int indexnext = 0; indexnext < subchildren.getLength(); ++indexnext) {
                    final Node next = subchildren.item(indexnext);
                    if (next.getNodeName().equals("objects")) {
                        while (next.hasChildNodes()) {
                            next.removeChild(next.getFirstChild());
                        }
                        next.appendChild(
                            next.getOwnerDocument().adoptNode(replacement.cloneNode(true))
                        );
                    }
                }
            }
        }
        return new EoRepresentation(new XMLDocument(program));
    }

    /**
     * Replace instructions.
     * @param clazz Class where to replace.
     * @param target What should be replaced.
     * @param replacement Replacement.
     * @todo #161:90min Refactor replace method.
     *  Right now it's a big method with a lot of repetition and high complexity.
     *  Moreover, some constants are hardcoded and it's not good.
     *  We need to refactor it into a set of smaller methods and remove all linter warnings.
     * @checkstyle ModifiedControlVariableCheck (200 lines)
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
                            Logger.info(
                                ImprovementDistilledObjects.class,
                                "Constructor inlining happened"
                            );
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
     * Replace arguments.
     * @param clazz Class where to replace.
     */
    private static void replaceArguments(final XmlClass clazz) {
        for (final XmlMethod method : clazz.methods()) {
            for (final XmlInstruction instruction : method.instructions()) {
                DecoratorPair.replaceArguments(
                    instruction.node(),
                    "org/eolang/jeo/B",
                    "org/eolang/jeo/AB"
                );
            }
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
            final String firstname = this.decorated.name();
            final Node first = new XMLDocument(
                new StringBuilder()
                    .append("<o base=\"opcode\" name=\"NEW-187-50\">")
                    .append("<o base=\"string\" data=\"bytes\">")
                    .append(new HexData(firstname.replace('.', '/')).value())
                    .append("</o>")
                    .append("</o>")
                    .toString()
            ).node().getFirstChild();
            final String secondname = this.decorator.name();
            final Node second = new XMLDocument(
                new StringBuilder()
                    .append("<o base=\"opcode\" name=\"NEW-187-50\">")
                    .append("<o base=\"string\" data=\"bytes\">")
                    .append(new HexData(secondname.replace('.', '/')).value())
                    .append("</o>")
                    .append("</o>")
                    .toString()
            ).node().getFirstChild();
            final Node dup = new XMLDocument("<o base=\"opcode\" name=\"DUP-89-53\"/>")
                .node()
                .getFirstChild();
            return Arrays.asList(
                new XmlInstruction(second),
                new XmlInstruction(dup),
                new XmlInstruction(first),
                new XmlInstruction(dup)
            );
        }

        /**
         * Replacement for NEW instruction.
         * @return Replacement.
         */
        private List<XmlInstruction> replacementNew() {
            final String newname = this.newname();
            final Node second = new XMLDocument(
                new StringBuilder()
                    .append("<o base=\"opcode\" name=\"NEW-187-50\">")
                    .append("<o base=\"string\" data=\"bytes\">")
                    .append(new HexData(newname.replace('.', '/')).value())
                    .append("</o>")
                    .append("</o>")
                    .toString()
            ).node().getFirstChild();
            final Node dup = new XMLDocument("<o base=\"opcode\" name=\"DUP-89-53\"/>")
                .node()
                .getFirstChild();
            return Arrays.asList(
                new XmlInstruction(second),
                new XmlInstruction(dup)
            );
        }

        /**
         * List of INVOKESPECIAL instructions that should be replaced.
         * @return List of INVOKESPECIAL instructions.
         */
        private List<XmlInstruction> targetSpecial() {
            return Arrays.asList(
                new XmlInstruction(
                    new XMLDocument(
                        new StringBuilder()
                            .append("<o base=\"opcode\" name=\"INVOKESPECIAL-183-55\">")
                            .append("<o base=\"string\" data=\"bytes\">")
                            .append(new HexData(this.decorated.name().replace('.', '/')).value())
                            .append("</o>")
                            .append("<o base=\"string\" data=\"bytes\">")
                            .append(new HexData("<init>").value())
                            .append("</o>")
                            .append("<o base=\"string\" data=\"bytes\">")
                            .append(new HexData("(I)V").value())
                            .append("</o>")
                            .append("</o>")
                            .toString()
                    ).node().getFirstChild()
                ),
                new XmlInstruction(
                    new XMLDocument(
                        new StringBuilder()
                            .append("<o base=\"opcode\" name=\"INVOKESPECIAL-183-55\">")
                            .append("<o base=\"string\" data=\"bytes\">")
                            .append(new HexData(this.decorator.name().replace('.', '/')).value())
                            .append("</o>")
                            .append("<o base=\"string\" data=\"bytes\">")
                            .append(new HexData("<init>").value())
                            .append("</o>")
                            .append("<o base=\"string\" data=\"bytes\">")
                            .append(new HexData("(Lorg/eolang/jeo/A;)V").value())
                            .append("</o>")
                            .append("</o>")
                            .toString()
                    ).node().getFirstChild()
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
                    new XMLDocument(
                        new StringBuilder()
                            .append("<o base=\"opcode\" name=\"INVOKESPECIAL-183-55\">")
                            .append("<o base=\"string\" data=\"bytes\">")
                            .append(new HexData(this.newname().replace('.', '/')).value())
                            .append("</o>")
                            .append("<o base=\"string\" data=\"bytes\">")
                            .append(new HexData("<init>").value())
                            .append("</o>")
                            .append("<o base=\"string\" data=\"bytes\">")
                            .append(new HexData("(I)V").value())
                            .append("</o>")
                            .append("</o>")
                            .toString()
                    ).node().getFirstChild()
                )
            );
        }

        /**
         * Combine two representations into one.
         * @return Combined representation.
         * @todo #157:90min Implement name generation for new combined representation.
         *  Right now we just concatenate the names of the two representations.
         *  Maybe we can leave the same scheme, but it's better to move into a separate class.
         */
        private Representation combine() {
            return new EoRepresentation(this.skeleton(this.decorator.toEO(), this.newname()));
        }

        /**
         * New name of the combined class.
         * @return New name.
         */
        private String newname() {
            final String second = this.decorator.name();
            return String.format(
                "%s%s",
                this.decorated.name(),
                second.substring(second.lastIndexOf('.') + 1)
            );
        }

        /**
         * Skeleton.
         * @param decor Decorator.
         * @param name Class name.
         * @return Combined XMIR representation.
         */
        private XML skeleton(final XML decor, final String name) {
            final List<XML> roots = decor.nodes("/program");
            final Node root = roots.get(0).node();
            final NamedNodeMap attributes = root.getAttributes();
            attributes.getNamedItem("name").setNodeValue(name);
            attributes.getNamedItem("time").setTextContent(
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
            );
            final NodeList children = root.getChildNodes();
            for (int index = 0; index < children.getLength(); ++index) {
                final Node item = children.item(index);
                if (item.getNodeName().equals("listing")) {
                    item.setTextContent("");
                }
                if (item.getNodeName().equals("objects")) {
                    this.handleObjects(item, name);
                }
            }
            return new XMLDocument(root);
        }

        /**
         * Handle all objects.
         * @param root Root node.
         * @param name Class name.
         */
        private void handleObjects(final Node root, final String name) {
            final NodeList children = root.getChildNodes();
            for (int index = 0; index < children.getLength(); ++index) {
                final Node item = children.item(index);
                if (item.getNodeName().equals("o")) {
                    final String bytename = name.replaceAll("\\.", "/");
                    item.getAttributes().getNamedItem("name").setNodeValue(bytename);
                    this.handleRootObject(item, bytename);
                }
            }
        }

        /**
         * Handle root object.
         * @param root Root node.
         * @param bytename Class name.
         */
        private void handleRootObject(final Node root, final String bytename) {
            final Document owner = root.getOwnerDocument();
            DecoratorPair.removeOldFields(root);
            DecoratorPair.removeOldConstructors(root);
            final XML original = this.decorated.toEO();
            final XmlClass clazz = new XmlClass(original);
            for (final XmlField field : clazz.fields()) {
                root.appendChild(owner.adoptNode(field.node().cloneNode(true)));
            }
            for (final XmlMethod method : clazz.methods()) {
                if (method.isConstructor()) {
                    final Node node = method.node();
                    final NodeList children = node.getChildNodes();
                    for (int index = 0; index < children.getLength(); ++index) {
                        final Node item = children.item(index);
                        final NamedNodeMap attrs = item.getAttributes();
                        if (attrs != null) {
                            final Node base = attrs.getNamedItem("base");
                            if (base != null) {
                                if (base.getNodeValue().equals("seq")) {
                                    final NodeList instructions = item.getChildNodes();
                                    for (int inst = 0; inst < instructions.getLength(); ++inst) {
                                        DecoratorPair.replaceArguments(
                                            instructions.item(inst),
                                            this.decorated.name().replace('.', '/'),
                                            bytename
                                        );
                                    }
                                }
                            }
                        }
                    }
                    root.appendChild(owner.adoptNode(node.cloneNode(true)));
                } else {
                    this.replaceMethodContent(root, method, bytename);
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
            DecoratorPair.objects(root).filter(
                node -> {
                    final NamedNodeMap attributes = node.getAttributes();
                    final Node base = attributes.getNamedItem("name");
                    return base != null && base.getNodeValue().equals("new");
                }
            ).forEach(root::removeChild);
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
            DecoratorPair.objects(root).filter(
                node -> {
                    final NamedNodeMap attributes = node.getAttributes();
                    final Node base = attributes.getNamedItem("base");
                    return base != null && base.getNodeValue().equals("field");
                }
            ).forEach(root::removeChild);
        }

        /**
         * Replace method content.
         * @param clazz Original class where to inline methods.
         * @param inlined Inlined method.
         * @param bytename Class name.
         * @checkstyle NestedIfDepthCheck (100 lines)
         * @checkstyle NestedForDepthCheck (100 lines)
         */
        private void replaceMethodContent(
            final Node clazz,
            final XmlMethod inlined,
            final String bytename
        ) {
            final List<XmlMethod> methods = new XmlClass(clazz).methods();
            final String old = this.decorated.name().replace('.', '/');
            for (final XmlMethod candidate : methods) {
                final List<XmlInstruction> instructions = candidate.instructions();
                final List<XmlInstruction> res = new ArrayList<>(0);
                for (final XmlInstruction instruction : instructions) {
                    final int code = instruction.code();
                    if (code != Opcodes.GETFIELD) {
                        if (code == Opcodes.INVOKEVIRTUAL) {
                            final List<XmlInstruction> tadam = inlined.instructions();
                            final Collection<XmlInstruction> filtered = new ArrayList<>(0);
                            for (final XmlInstruction xmlinstr : tadam) {
                                final int codee = xmlinstr.code();
                                DecoratorPair.replaceArguments(
                                    xmlinstr.node(),
                                    old,
                                    bytename
                                );
                                if (codee != Opcodes.RETURN && codee != Opcodes.IRETURN
                                    && codee != Opcodes.ALOAD) {
                                    filtered.add(xmlinstr);
                                }
                            }
                            res.addAll(filtered);
                        } else {
                            res.add(instruction);
                        }
                    }
                }
                DecoratorPair.setInstructions(candidate.node(), res);
            }
        }

        /**
         * Replace arguments.
         * @param node Instruction.
         * @param oldname Old class name.
         * @param newname New class name.
         * @todo #157:90min Handle arguments correctly during inlining optimization.
         *  Right now we just replace all arguments with the new class name.
         *  It's not correct, because we need to handle arguments correctly.
         */
        private static void replaceArguments(
            final Node node,
            final String oldname,
            final String newname
        ) {
            final NodeList children = node.getChildNodes();
            for (int index = 0; index < children.getLength(); ++index) {
                final Node child = children.item(index);
                if (child.getNodeName().equals("o")) {
                    final String old = new HexData(oldname).value();
                    final String content = child.getTextContent();
                    if (old.equals(content)) {
                        child.setTextContent(new HexData(newname).value());
                    }
                }
            }
        }

        /**
         * Set instructions.
         * @param method Method.
         * @param res Instructions.
         * @todo #161:90min Move setInstructions method.
         *  Right now we implemented this method inside ImprovementDistilledObjects class.
         *  But it's better to move it into a XmlMethod class. Moreover, this method is
         *  overcomplicated, so it makes sense to refactor it and remove all linter warnings.
         * @checkstyle NestedIfDepthCheck (100 lines)
         */
        private static void setInstructions(final Node method, final List<XmlInstruction> res) {
            final NodeList children = method.getChildNodes();
            for (int index = 0; index < children.getLength(); ++index) {
                final Node seq = children.item(index);
                if (seq.getNodeName().equals("o")) {
                    final NamedNodeMap attributes = seq.getAttributes();
                    if (attributes != null) {
                        final Node base = attributes.getNamedItem("base");
                        if (base != null) {
                            if (base.getNodeValue().equals("seq")) {
                                while (seq.hasChildNodes()) {
                                    seq.removeChild(seq.getFirstChild());
                                }
                                for (final XmlInstruction instruction : res) {
                                    final Node node = instruction.node();
                                    seq.appendChild(
                                        seq.getOwnerDocument().adoptNode(node.cloneNode(true))
                                    );
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Objects.
         * @param root Root node.
         * @return Stream of class objects.
         */
        private static Stream<Node> objects(final Node root) {
            final NodeList children = root.getChildNodes();
            final List<Node> res = new ArrayList<>(children.getLength());
            for (int index = 0; index < children.getLength(); ++index) {
                final Node child = children.item(index);
                if (child.getNodeName().equals("o")) {
                    res.add(child);
                }
            }
            return res.stream();
        }

        /**
         * Find sequence node.
         * @param node Node.
         * @return Sequence node.
         */
        private static Optional<Node> sequence(final Node node) {
            Optional<Node> result = Optional.empty();
            final NodeList children = node.getChildNodes();
            for (int index = 0; index < children.getLength(); ++index) {
                final Node item = children.item(index);
                final NamedNodeMap attributes = item.getAttributes();
                if (attributes == null) {
                    continue;
                }
                final Node base = attributes.getNamedItem("base");
                if (base == null) {
                    continue;
                }
                if (base.getNodeValue().equals("seq")) {
                    result = Optional.of(item);
                    break;
                }
            }
            return result;
        }

        /**
         * Check if node is an instruction.
         * @param node Node.
         * @return True if node is an instruction.
         */
        private static boolean isInstruction(final Node node) {
            final boolean result;
            final NamedNodeMap attrs = node.getAttributes();
            if (attrs == null || attrs.getNamedItem("name") == null) {
                result = false;
            } else {
                result = true;
            }
            return result;
        }
    }
}
