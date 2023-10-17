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
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML field.
 * @since 0.1
 */
public class XmlField {

    /**
     * Field node.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    private final Node node;

    /**
     * Constructor.
     * @param node Field node.
     */
    XmlField(final Node node) {
        this.node = node;
    }

    /**
     * Field name.
     * @return Name.
     */
    public String name() {
        return this.node.getAttributes().getNamedItem("name").getNodeValue();
    }

    /**
     * Field access modifiers.
     * @return Access modifiers.
     */
    public int access() {
        return this.find("access").map(HexString::decodeAsInt).orElse(0);
    }

    /**
     * Field descriptor.
     * @return Descriptor.
     */
    public String descriptor() {
        return this.find("descriptor").map(HexString::decode).orElse(null);
    }

    /**
     * Field signature.
     * @return Signature.
     */
    public String signature() {
        return this.find("signature").map(HexString::decode).orElse(null);
    }

    /**
     * Field value.
     * @return Value.
     */
    public Object value() {
        return this.find("value").map(HexString::decode).orElse(null);
    }

    /**
     * XML node.
     * @return Node node.
     * @todo #157:90min Hide internal node representation in XmlField.
     *  This class should not expose internal node representation.
     *  We have to consider to add methods or classes in order to avoid
     *  exposing internal node representation.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    public Node node() {
        return this.node;
    }

    /**
     * Find node text by key.
     * @param key Key.
     * @return Text.
     */
    private Optional<HexString> find(final String key) {
        return this.children()
            .filter(
                object -> object.getAttributes()
                    .getNamedItem("name")
                    .getNodeValue()
                    .equals(key)
            )
            .findFirst()
            .map(Node::getTextContent)
            .filter(text -> !text.isEmpty())
            .map(HexString::new);
    }

    /**
     * Current node child objects.
     * @return Child objects.
     */
    private Stream<Node> children() {
        final NodeList childs = this.node.getChildNodes();
        final List<Node> res = new ArrayList<>(childs.getLength());
        for (int identifier = 0; identifier < childs.getLength(); ++identifier) {
            final Node child = childs.item(identifier);
            if (child.getNodeName().equals("o")) {
                res.add(child);
            }
        }
        return res.stream();
    }
}
