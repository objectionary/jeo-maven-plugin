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
package org.eolang.jeo.representation.asm;

import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.asm.generation.BytecodeClassProperties;
import org.w3c.dom.Node;

/**
 * XML representation of a class.
 *
 * @since 0.1.0
 */
final class XmlClassProperties {

    /**
     * XML representation of a class.
     */
    private final XMLDocument clazz;

    /**
     * Constructor.
     * @param clazz XMl representation of a class.
     */
    XmlClassProperties(final Node clazz) {
        this.clazz = new XMLDocument(clazz);
    }

    /**
     * Retrieve 'access' modifiers of a class.
     * @return Access modifiers.
     */
    int access() {
        return new HexString(this.clazz.xpath("//o[@name='access']/text()").get(0)).decodeAsInt();
    }

    String signature() {
        return new HexString(this.clazz.xpath("//o[@name='signature']/text()").get(0)).decode();
    }

    private String supername() {
        return new HexString(this.clazz.xpath("//o[@name='supername']/text()").get(0)).decode();
    }

    private String[] interfaces() {
        return new String[0];
    }

    BytecodeClassProperties toBytecodeProperties() {
        return new BytecodeClassProperties(
            this.access(),
            this.signature(),
            this.supername(),
            this.interfaces()
        );
    }
}

