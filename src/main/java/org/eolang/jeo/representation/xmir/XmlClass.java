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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML class.
 * @since 0.1
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class XmlClass {

    /**
     * Class node from entire XML.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    private final Node node;

    /**
     * Constructor.
     * @param xml Class node.
     */
    public XmlClass(final Node xml) {
        this.node = xml;
    }

    /**
     * Retrieve all constructors from XMIR.
     * @return List of constructors.
     */
    public List<XmlMethod> constructors() {
        return this.objects().filter(
            xmirnode -> {
                final Node base = xmirnode.getAttributes().getNamedItem("name");
                return base != null && "new".equals(base.getNodeValue());
            }
        ).map(XmlMethod::new).collect(Collectors.toList());
    }

    /**
     * Methods.
     * @return Class methods.
     */
    public List<XmlMethod> methods() {
        return this.objects()
            .filter(o -> o.getAttributes().getNamedItem("base") == null)
            .map(XmlMethod::new)
            .collect(Collectors.toList());
    }

    /**
     * Fields.
     * @return Class fields.
     */
    public List<XmlField> fields() {
        return this.objects()
            .filter(o -> o.getAttributes().getNamedItem("base") != null)
            .filter(o -> "field".equals(o.getAttributes().getNamedItem("base").getNodeValue()))
            .map(XmlField::new)
            .collect(Collectors.toList());
    }

    /**
     * Set class name in XML.
     * @param name Name of the Class.
     * @return Class.
     */
    public XmlClass withName(final String name) {
        new XmlNode(this.node).withAttribute("name", name);
        return this;
    }

    /**
     * Add field.
     * @param field Field.
     * @return The same class.
     */
    public XmlClass withField(final XmlField field) {
        final Document owner = this.node.getOwnerDocument();
        this.node.appendChild(owner.adoptNode(field.node().cloneNode(true)));
        return this;
    }

    /**
     * Add constructor.
     * @param method Constructor.
     */
    public void withConstructor(final XmlMethod method) {
        final Document owner = this.node.getOwnerDocument();
        this.node.appendChild(owner.adoptNode(method.node().cloneNode(true)));
    }

    /**
     * Internal XML node.
     * @return Internal XML node.
     * @todo #161:30min Hide internal node representation in XmlClass.
     *  This class should not expose internal node representation.
     *  We have to consider to add methods or classes in order to avoid
     *  exposing internal node representation.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    public Node node() {
        return this.node;
    }

    /**
     * Try to inline method into all possible places.
     * @param method Other method.
     */
    public void inline(final XmlMethod method) {
        this.methods().forEach(inner -> inner.inline(method));
    }

    /**
     * Replace all instruction arguments that have "old" value with "replacement".
     * @param old Old value.
     * @param replacement Replacement value.
     */
    public void replaceArguments(final String old, final String replacement) {
        this.methods()
            .stream()
            .map(XmlMethod::instructions)
            .flatMap(Collection::stream)
            .forEach(instruction -> instruction.replaceArguementsValues(old, replacement));
    }

    /**
     * Class name.
     * @return Name.
     */
    String name() {
        return String.valueOf(this.node.getAttributes().getNamedItem("name").getTextContent());
    }

    /**
     * Class properties.
     * @return Class properties.
     */
    XmlClassProperties properties() {
        return new XmlClassProperties(this.node);
    }

    /**
     * Objects.
     * @return Stream of class objects.
     */
    private Stream<Node> objects() {
        final NodeList children = this.node.getChildNodes();
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
