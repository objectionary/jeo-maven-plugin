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

import com.jcabi.log.Logger;
import com.jcabi.xml.XMLDocument;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML method.
 * @since 0.1
 */
public final class XmlMethod {

    /**
     * Method node.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    private final Node node;

    /**
     * Constructor.
     * @param node Method node.
     */
    public XmlMethod(final Node node) {
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
     * Method instructions.
     * @return Instructions.
     */
    @SafeVarargs
    public final List<XmlInstruction> instructions(Predicate<XmlInstruction>... predicates) {
        final List<XmlInstruction> result;
        final Optional<Node> sequence = this.sequence();
        if (sequence.isPresent()) {
            final Node seq = sequence.get();
            final List<XmlInstruction> instructions = new ArrayList<>(0);
            for (int index = 0; index < seq.getChildNodes().getLength(); ++index) {
                final Node instruction = seq.getChildNodes().item(index);
                if (XmlMethod.isInstruction(instruction)) {
                    if (Arrays.stream(predicates).allMatch(
                        predicate -> predicate.test(new XmlInstruction(instruction)))) {
                        instructions.add(new XmlInstruction(instruction));
                    }
                }
            }
            result = instructions;
        } else {
            result = Collections.emptyList();
        }
        return result;
    }

    /**
     * Set instructions for method.
     * @param updated New instructions.
     * @todo #176:60min Add unit test for 'setInstructions' method.
     *  Currently we don't have a unit test for XmlMethod. We should create a testing class
     *  and test 'setInstructions' method. Don't forget to remove the puzzle for that method.
     */
    public void setInstructions(final List<XmlInstruction> updated) {
        final Node root = this.sequence().orElseThrow(
            () -> new IllegalStateException(
                String.format("Can't find bytecode of the method %s", new XMLDocument(this.node))
            )
        );
        final Document owner = root.getOwnerDocument();
        Logger.info(
            this,
            String.format(
                "Set new method instructions %n%s%n",
                updated.stream().map(XmlInstruction::toString).collect(Collectors.joining("\n"))
            )
        );
        while (root.hasChildNodes()) {
            root.removeChild(root.getFirstChild());
        }
        for (final XmlInstruction instruction : updated) {
            root.appendChild(owner.adoptNode(instruction.node()));
        }
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
