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
package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XMLDocument;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.ToString;
import org.eolang.jeo.representation.HexData;
import org.objectweb.asm.Opcodes;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML method.
 * @since 0.1
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class XmlMethod {

    /**
     * Method node.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    private final Node node;

    /**
     * Constructor.
     * @param name Method name.
     * @param access Access modifiers.
     * @param descriptor Method descriptor.
     * @param entries Method instructions.
     */
    public XmlMethod(
        final String name,
        final int access,
        final String descriptor,
        final XmlBytecodeEntry... entries
    ) {
        this(
            new XMLDocument(
                String.join(
                    "",
                    String.format("<o name='%s'>", name),
                    String.format(
                        "<o base='int' data='bytes' name='access'>%s</o>",
                        new HexData(access).value()
                    ),
                    String.format(
                        "<o base='string' data='bytes' name='descriptor'>%s</o>",
                        new HexData(descriptor).value()
                    ),
                    "<o base='string' data='bytes' name='signature'/>",
                    "<o base='tuple' data='tuple' name='exceptions'/>",
                    "<o base='seq' name='@'>",
                    Arrays.stream(entries)
                        .map(e -> new XMLDocument(e.node()).toString())
                        .collect(Collectors.joining()),
                    "</o>",
                    "</o>"
                )
            ).node().getFirstChild()
        );
    }

    /**
     * Constructor.
     * @param xml XML node as String.
     */
    XmlMethod(final String... xml) {
        this(new XMLDocument(String.join("", xml)).node());
    }

    /**
     * Constructor.
     * @param node Method node.
     */
    XmlMethod(final Node node) {
        this.node = node;
    }

    /**
     * Method name.
     * @return Name.
     */
    public String name() {
        return String.valueOf(new XMLDocument(this.node).xpath("./@name").get(0));
    }

    /**
     * Method access modifiers.
     * @return Access modifiers.
     */
    public int access() {
        return new HexString(
            new XMLDocument(this.node).xpath("./o[@name='access']/text()").get(0)
        ).decodeAsInt();
    }

    /**
     * Method descriptor.
     * @return Descriptor.
     */
    public String descriptor() {
        return new HexString(
            new XMLDocument(this.node).xpath("./o[@name='descriptor']/text()").get(0)
        ).decode();
    }

    /**
     * XML node.
     * @return XML node.
     * @todo #157:90min Hide internal node representation in XmlMethod.
     *  This class should not expose internal node representation.
     *  We have to consider to add methods or classes in order to avoid
     *  exposing internal node representation.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    public Node node() {
        return this.node;
    }

    /**
     * Checks if method is a constructor.
     * @return True if method is a constructor.
     */
    public boolean isConstructor() {
        return this.node.getAttributes().getNamedItem("name").getNodeValue().equals("new");
    }

    /**
     * All method instructions.
     * @param predicates Predicates to filter instructions.
     * @return Instructions.
     */
    @SafeVarargs
    public final List<XmlBytecodeEntry> instructions(
        final Predicate<XmlBytecodeEntry>... predicates
    ) {
        return new XmlNode(this.node).child("base", "seq")
            .children()
            .filter(element -> element.attribute("base").isPresent())
            .map(XmlNode::toCommand)
            .filter(instr -> Arrays.stream(predicates).allMatch(predicate -> predicate.test(instr)))
            .collect(Collectors.toList());
    }

    /**
     * Copy method node.
     * @return Instructions.
     */
    public XmlMethod copy() {
        return new XmlMethod(this.node.cloneNode(true));
    }

    /**
     * Retrieves the list of all invocations of other object methods.
     * Usually they are invoke virtual instructions which look like this:
     * - GET FIELD: foo
     * - LIST OF ARGUMENTS
     * - INVOKEVIRTUAL: bar
     * This list represents the following command:
     * foo.bar(a, b);
     * @return List of invocations.
     */
    public List<XmlInvokeVirtual> invokeVirtuals() {
        final List<XmlBytecodeEntry> all = this.instructions();
        final List<XmlInvokeVirtual> res = new ArrayList<>(0);
        for (int index = 0; index < all.size(); ++index) {
            final XmlBytecodeEntry top = all.get(index);
            if (top.hasOpcode(Opcodes.GETFIELD)) {
                for (int inner = index + 1; inner < all.size(); ++inner) {
                    final XmlBytecodeEntry bottom = all.get(inner);
                    if (bottom.hasOpcode(Opcodes.INVOKEVIRTUAL)) {
                        res.add(new XmlInvokeVirtual(all.subList(index, inner + 1)));
                    }
                }
            }
        }
        return res;
    }

    /**
     * Inline all method invocations.
     * @param inline Method to inline.
     */
    public void inline(final XmlMethod inline) {
        final List<XmlInvokeVirtual> invocations = this.invokeVirtuals();
        final Set<XmlBytecodeEntry> ignored = invocations.stream()
            .map(XmlInvokeVirtual::field)
            .collect(Collectors.toSet());
        final Set<XmlBytecodeEntry> where = invocations.stream()
            .map(XmlInvokeVirtual::invocation)
            .collect(Collectors.toSet());
        final List<XmlBytecodeEntry> body = new ArrayList<>(0);
        for (final XmlBytecodeEntry instruction : this.instructions()) {
            if (!ignored.contains(instruction)) {
                if (where.contains(instruction)) {
                    inline.instructionsToInline().forEach(body::add);
                } else {
                    body.add(instruction);
                }
            }
        }
        this.setInstructions(body);
    }

    /**
     * Set instructions for method.
     * @param updated New instructions.
     * @todo #176:60min Add unit test for 'setInstructions' method.
     *  Currently we don't have a unit test for XmlMethod. We should create a testing class
     *  and test 'setInstructions' method. Don't forget to remove the puzzle for that method.
     */
    public void setInstructions(final List<XmlBytecodeEntry> updated) {
        final Node root = this.sequence().orElseThrow(
            () -> new IllegalStateException(
                String.format("Can't find bytecode of the method %s", new XMLDocument(this.node))
            )
        );
        while (root.hasChildNodes()) {
            root.removeChild(root.getFirstChild());
        }
        for (final XmlBytecodeEntry instruction : updated) {
            root.appendChild(root.getOwnerDocument().adoptNode(instruction.node()));
        }
    }

    @Override
    public String toString() {
        return new XMLDocument(this.node).toString();
    }

    /**
     * Method instructions that might be inlined.
     * @return Instructions.
     * @todo #228:90min. Create more suitable method for determining instructions to inline.
     *  Currently we are using a method that returns all instructions except return statements,
     *  which is working for simple examples, but might fail on more complex cases.
     *  We have to write more tests and create a more suitable method for determining instructions
     *  to inline.
     */
    private Stream<XmlBytecodeEntry> instructionsToInline() {
        return this.instructions(new Without(Opcodes.RETURN, Opcodes.IRETURN, Opcodes.ALOAD))
            .stream();
    }

    /**
     * Find sequence node.
     * @return Sequence node.
     */
    private Optional<Node> sequence() {
        Optional<Node> result = Optional.empty();
        final NodeList children = this.node.getChildNodes();
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
     * Predicated for filtering commands.
     * Filters commands that have specified opcodes.
     * @since 0.1
     */
    public static final class Without implements Predicate<XmlBytecodeEntry> {

        /**
         * Opcodes to exclude.
         */
        private final int[] opcodes;

        /**
         * Constructor.
         * @param opcodes Opcodes to exclude.
         */
        public Without(final int... opcodes) {
            this.opcodes = Arrays.copyOf(opcodes, opcodes.length);
        }

        @Override
        public boolean test(final XmlBytecodeEntry instr) {
            return Arrays.stream(this.opcodes).noneMatch(instr::hasOpcode);
        }
    }
}
