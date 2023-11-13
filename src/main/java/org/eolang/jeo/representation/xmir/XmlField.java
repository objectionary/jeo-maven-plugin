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

import java.util.Optional;
import java.util.stream.Collectors;
import org.w3c.dom.Node;

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
        return this.find(Attribute.ACCESS).map(HexString::decodeAsInt).orElse(0);
    }

    /**
     * Field descriptor.
     * @return Descriptor.
     */
    public String descriptor() {
        return this.find(Attribute.DESCRIPTOR).map(HexString::decode).orElse(null);
    }

    /**
     * Field signature.
     * @return Signature.
     */
    public String signature() {
        return this.find(Attribute.SIGNATURE).map(HexString::decode).orElse(null);
    }

    /**
     * Field value.
     * @return Value.
     */
    public Object value() {
        return this.find(Attribute.VALUE).map(HexString::decode).orElse(null);
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
     * Find node text by attribute.
     * @param attribute Attribute.
     * @return Text.
     */
    private Optional<HexString> find(final Attribute attribute) {
        final String text = new XmlNode(this.node).children()
            .map(XmlNode::text)
            .collect(Collectors.toList())
            .get(attribute.ordinal());
        final Optional<HexString> result;
        if (text.isEmpty()) {
            result = Optional.empty();
        } else {
            result = Optional.of(new HexString(text));
        }
        return result;
    }

    /**
     * Field attribute.
     * Pay attention that the order of the attributes is important.
     * They should be in the same order as in the XML representation.
     * @since 0.1
     */
    private enum Attribute {

        /**
         * Access modifier.
         * It's a number that represents sum of access modifiers.
         * For example, if the field is public and static, then the value will be 9.
         * See {@link org.objectweb.asm.Opcodes} for more details.
         */
        ACCESS,

        /**
         * Field descriptor.
         * For example, for field of type int the descriptor will be "I".
         */
        DESCRIPTOR,

        /**
         * Field signature.
         */
        SIGNATURE,

        /**
         * Initial field value.
         * May not be set.
         */
        VALUE;
    }
}
