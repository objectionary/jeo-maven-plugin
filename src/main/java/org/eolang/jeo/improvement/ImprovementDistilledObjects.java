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
 * @todo #157:90min Replace new B(new A()) with new AB().
 *  Right now we just compose a new object with name AB. But it's not enough.
 *  We have to replace all the old objects created with new B(new A()) with new AB().
 * @todo #157:90min ImprovementDistilledObjects is needed to be refactored.
 *  Right now it's a big class with a lot of methods, repetition and high complexity.
 *  We need to refactor it into a set of smaller classes and remove all
 *  linter warnings.
 */
public final class ImprovementDistilledObjects implements Improvement {
    @Override
    public Collection<? extends Representation> apply(
        final Collection<? extends Representation> representations
    ) {
        final List<Representation> additional = ImprovementDistilledObjects
            .decorators(
                representations.stream()
                    .sorted(Comparator.comparing((Representation ir) -> ir.name()))
                    .collect(Collectors.toList())
            ).stream()
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
            representations.stream(),
            additional.stream()
        ).collect(Collectors.toList());
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
         * Combine two representations into one.
         * @return Combined representation.
         * @todo #157:90min Implement name generation for new combined representation.
         *  Right now we just concatenate the names of the two representations.
         *  Maybe we can leave the same scheme, but it's better to move into a separate class.
         */
        private Representation combine() {
            final String second = this.decorator.name();
            final XML decor = this.decorator.toEO();
            final String name = String.format(
                "%s%s",
                this.decorated.name(),
                second.substring(second.lastIndexOf('.') + 1)
            );
            return new EoRepresentation(this.skeleton(decor, name));
        }

        private XML skeleton(final XML decor, final String name) {
            final List<XML> roots = decor.nodes("/program");
            final Node root = roots.get(0).node();
            final NamedNodeMap attributes = root.getAttributes();
            attributes.getNamedItem("name").setNodeValue(name);
            attributes.getNamedItem("time").setTextContent(
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
            );
            final NodeList children = root.getChildNodes();
            for (int index = 0; index < children.getLength(); index++) {
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

        private void handleObjects(final Node root, final String name) {
            final NodeList children = root.getChildNodes();
            for (int index = 0; index < children.getLength(); index++) {
                final Node item = children.item(index);
                if (item.getNodeName().equals("o")) {
                    final String bytename = name.replaceAll("\\.", "/");
                    item.getAttributes().getNamedItem("name").setNodeValue(bytename);
                    this.handleRootObject(item, bytename);
                }
            }
        }

        private void handleRootObject(final Node root, final String bytename) {
            final Document owner = root.getOwnerDocument();
            this.removeOldFields(root);
            this.removeOldConstructors(root);
            final XML original = this.decorated.toEO();
            final XmlClass xmlClass = new XmlClass(original);
            for (final XmlField field : xmlClass.fields()) {
                root.appendChild(owner.adoptNode(field.node().cloneNode(true)));
            }
            for (final XmlMethod method : xmlClass.methods()) {
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
                                        this.replaceArguments(
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
        private void removeOldConstructors(final Node root) {
            this.objects(root).filter(
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
        private void removeOldFields(final Node root) {
            this.objects(root).filter(
                node -> {
                    final NamedNodeMap attributes = node.getAttributes();
                    final Node base = attributes.getNamedItem("base");
                    return base != null && base.getNodeValue().equals("field");
                }
            ).forEach(root::removeChild);
        }

        private void replaceMethodContent(
            final Node root,
            final XmlMethod method,
            final String bytename
        ) {
            final List<Node> methods = this.methods(root);
            final String old = this.decorated.name().replace('.', '/');
            for (final Node high : methods) {
                final List<XmlInstruction> instructions = ImprovementDistilledObjects.instructions(
                    high
                );
                List<XmlInstruction> res = new ArrayList<>(0);
                for (final XmlInstruction instruction : instructions) {
                    final int code = instruction.code();
                    if (code != Opcodes.GETFIELD) {
                        if (code == Opcodes.INVOKEVIRTUAL) {
                            final List<XmlInstruction> tadam = method.instructions();
                            final List<XmlInstruction> filtered = new ArrayList<>(0);
                            for (final XmlInstruction xmlInstruction : tadam) {
                                final int codee = xmlInstruction.code();
                                this.replaceArguments(xmlInstruction.node(), old, bytename);
                                if (codee != Opcodes.RETURN && codee != Opcodes.IRETURN
                                    && codee != Opcodes.ALOAD) {
                                    filtered.add(xmlInstruction);
                                }
                            }
                            res.addAll(filtered);
                        } else {
                            res.add(instruction);
                        }
                    }
                }
                this.setInstructions(high, res);
            }
        }

        /**
         * Replace arguments.
         * @param node Instruction.
         * @param oldname  Old class name.
         * @param bytename New class name.
         * @todo #157:90min Handle arguments correctly during inlining optimization.
         *  Right now we just replace all arguments with the new class name.
         *  It's not correct, because we need to handle arguments correctly.
         */
        private void replaceArguments(
            final Node node,
            final String oldname,
            final String bytename
        ) {
            final NodeList children = node.getChildNodes();
            for (int index = 0; index < children.getLength(); ++index) {
                final Node child = children.item(index);
                if (child.getNodeName().equals("o")) {
                    final String old = new HexData(oldname).value();
                    final String content = child.getTextContent();
                    if (old.equals(content)) {
                        child.setTextContent(new HexData(bytename).value());
                    }
                }
            }
        }

        /**
         * Set instructions.
         * @param method Method.
         * @param res Instructions.
         */
        private void setInstructions(final Node method, final List<XmlInstruction> res) {
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
                                        seq.getOwnerDocument().adoptNode(node.cloneNode(true)));
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Methods.
         * @return Class methods.
         * @todo #157:90min Code duplication.
         *  There is a code duplication between classes:
         *  ImprovementDistilledObjects and XmlClass.
         *  We need to extract the common code into a separate class or just to use XmlClass.
         */
        private List<Node> methods(final Node root) {
            return this.objects(root)
                .filter(o -> o.getAttributes().getNamedItem("base") == null)
                .filter(method -> !new XmlMethod(method).isConstructor())
                .collect(Collectors.toList());
        }

        /**
         * Objects.
         * @return Stream of class objects.
         */
        private Stream<Node> objects(final Node root) {
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
    }

    /**
     * Method instructions.
     * @return Instructions.
     */
    public static List<XmlInstruction> instructions(final Node node) {
        final List<XmlInstruction> result;
        final Optional<Node> sequence = ImprovementDistilledObjects.sequence(node);
        if (sequence.isPresent()) {
            final Node seq = sequence.get();
            final List<XmlInstruction> instructions = new ArrayList<>(0);
            final NodeList children = seq.getChildNodes();
            final int length = children.getLength();
            for (int index = 0; index < length; ++index) {
                final Node instruction = children.item(index);
                if (ImprovementDistilledObjects.isInstruction(instruction)) {
                    instructions.add(new XmlInstruction(instruction));
                }
            }
            result = instructions;
        } else {
            result = Collections.emptyList();
        }
        return result;
    }

    /**
     * Find sequence node.
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
