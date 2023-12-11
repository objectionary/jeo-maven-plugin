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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeMethodProperties;
import org.eolang.jeo.representation.directives.DirectivesMethod;
import org.eolang.jeo.representation.directives.DirectivesMethodProperties;
import org.w3c.dom.Node;
import org.xembly.Transformers;
import org.xembly.Xembler;

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
     * @param name Method name.
     * @param access Access modifiers.
     * @param descriptor Method descriptor.
     * @param exceptions Method exceptions.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public XmlMethod(
        final String name,
        final int access,
        final String descriptor,
        final String... exceptions
    ) {
        this(XmlMethod.prestructor(name, access, descriptor, exceptions));
    }

    /**
     * Constructor.
     * @param node Method node.
     */
    XmlMethod(final XmlNode node) {
        this(node.node());
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
        final String result;
        final String original = String.valueOf(new XMLDocument(this.node).xpath("./@name").get(0));
        if ("new".equals(original)) {
            result = "<init>";
        } else {
            result = original;
        }
        return result;
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
     * Method signature.
     * @return Signature.
     */
    public String signature() {
        return new XMLDocument(this.node).xpath("./o[@name='signature']/text()")
            .stream()
            .filter(s -> !s.isBlank())
            .findFirst()
            .map(HexString::new)
            .map(HexString::decode)
            .orElse(null);
    }

    /**
     * Method trycatch entries.
     * @return Trycatch entries.
     */
    public List<XmlTryCatchEntry> trycatchEntries() {
        final XmlNode xmlnode = new XmlNode(this.node);
        if(xmlnode.hasAttribute("name", "trycatchblocks")) {
            return xmlnode
                .child("name", "trycatchblocks")
                .children()
                .map(XmlTryCatchEntry::new)
                .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Method exceptions.
     * @return Exceptions.
     */
    private String[] exceptions() {
        return new XMLDocument(this.node)
            .xpath("./o[@name='exceptions']/o/text()")
            .stream()
            .map(HexString::new)
            .map(HexString::decode)
            .toArray(String[]::new);
    }

    /**
     * Method properties as bytecode.
     * @return Properties.
     */
    public BytecodeMethodProperties properties() {
        return new BytecodeMethodProperties(
            this.access(),
            this.name(),
            this.descriptor(),
            this.signature(),
            this.exceptions()
        );
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

    @Override
    public String toString() {
        return new XMLDocument(this.node).toString();
    }

    /**
     * Create Method XmlNode by directives.
     * @param name Method name.
     * @param access Method access modifiers.
     * @param descriptor Method descriptor.
     * @return Method XmlNode.
     */
    private static XmlNode prestructor(
        final String name,
        final int access,
        final String descriptor,
        final String... exceptions
    ) {
        return new XmlNode(
            new Xembler(
                new DirectivesMethod(
                    name,
                    new DirectivesMethodProperties(access, descriptor, "", exceptions)
                ),
                new Transformers.Node()
            ).xmlQuietly()
        );
    }
}
