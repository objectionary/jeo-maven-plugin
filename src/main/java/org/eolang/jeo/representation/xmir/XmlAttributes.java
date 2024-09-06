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

import org.eolang.jeo.representation.bytecode.BytecodeClass;

/**
 * Xml representation of a class attributes.
 * @since 0.4
 * @todo #589:30min Add Unit Tests for XmlAttributes class.
 *  XmlAttributes class is not covered by unit tests.
 *  Add unit tests for XmlAttributes class in order to increase the code coverage
 *  and improve the quality of the code.
 */
public final class XmlAttributes {

    /**
     * XML node of attributes.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param node XML node.
     */
    XmlAttributes(final XmlNode node) {
        this.node = node;
    }

    /**
     * Write to bytecode.
     * @param bytecode Bytecode where to write.
     */
    public void writeTo(final BytecodeClass bytecode) {
        this.node.children().map(XmlAttribute::new).forEach(attr -> attr.writeTo(bytecode));
    }
}
