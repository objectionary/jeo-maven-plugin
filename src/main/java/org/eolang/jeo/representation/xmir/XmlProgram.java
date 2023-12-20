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

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.directives.DirectivesClass;
import org.eolang.jeo.representation.directives.DirectivesProgram;
import org.w3c.dom.Node;
import org.xembly.Xembler;

/**
 * XMIR Program.
 * @since 0.1
 */
public final class XmlProgram {

    /**
     * Program node name.
     */
    private static final String PROGRAM = "program";

    /**
     * Root node.
     */
    private final Node root;

    /**
     * Constructor.
     * @param name Class name.
     */
    public XmlProgram(final ClassName name) {
        this(
            new XMLDocument(
                new Xembler(
                    new DirectivesProgram().withClass(name, new DirectivesClass(name))
                ).xmlQuietly()
            ).node()
        );
    }

    /**
     * Constructor.
     * @param xml Raw XMIR.
     */
    public XmlProgram(final XML xml) {
        this(xml.node());
    }

    /**
     * Constructor.
     * @param root Root node.
     */
    public XmlProgram(final Node root) {
        this.root = root;
    }

    /**
     * Find top-level class.
     * @return Class.
     */
    public XmlClass top() {
        return new XmlNode(this.root)
            .child(XmlProgram.PROGRAM)
            .child("objects")
            .child("o")
            .toClass();
    }

    /**
     * Retrieve program package.
     * @return Package.
     */
    String pckg() {
        return new XmlNode(this.root)
            .child(XmlProgram.PROGRAM)
            .child("metas")
            .child("meta")
            .child("tail")
            .text();
    }
}
