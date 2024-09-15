/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.DataType;

/**
 * XML operand.
 * @since 0.3
 */
@ToString
@EqualsAndHashCode
public final class XmlOperand {

    /**
     * Raw XML node which represents an instruction operand.
     */
    @EqualsAndHashCode.Exclude
    private final XmlNode raw;

    /**
     * Constructor.
     * @param node Raw XML operand node.
     */
    XmlOperand(final XmlNode node) {
        this.raw = node;
    }

    /**
     * Convert XML operand to an object.
     * @return Object.
     */
    @EqualsAndHashCode.Include
    public Object asObject() {
        final String base = this.raw.attribute("base")
            .orElseThrow(
                () -> new IllegalStateException(
                    String.format(
                        "'%s' is not an argument because it doesn't have 'base' attribute",
                        this.raw
                    )
                )
            );
        final Object result;
        if ("handle".equals(base)) {
            result = new XmlHandler(this.raw).bytecode().asHandle();
        } else if ("annotation".equals(base)) {
            result = new XmlAnnotation(this.raw).bytecode();
        } else if ("annotation-property".equals(base)) {
            final XmlAnnotationProperty xml = new XmlAnnotationProperty(this.raw);
            result = xml.bytecode();
        } else if ("tuple".equals(base)) {
            result = new XmlTuple(this.raw).asObject();
        } else {
            if (this.raw.attribute("scope")
                .map("nullable"::equals)
                .orElse(false)) {
                result = null;
            } else {
                result = DataType.find(base).decode(this.raw.text());
            }
        }
        return result;
    }
}
