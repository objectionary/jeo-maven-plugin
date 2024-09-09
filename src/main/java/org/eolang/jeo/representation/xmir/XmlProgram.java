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
import org.eolang.jeo.representation.directives.DirectivesMetas;
import org.eolang.jeo.representation.directives.DirectivesProgram;
import org.w3c.dom.Node;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * XMIR Program.
 *
 * @since 0.1
 */
public final class XmlProgram {
    //TODO: WE NEED REFACTORING HERE!

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
     *
     * @param name Class name.
     */
    public XmlProgram(final ClassName name) {
        this(
            new XMLDocument(
                new Xembler(
                    new DirectivesProgram().withClass(
                        new DirectivesMetas(name),
                        new DirectivesClass(name)
                    )
                ).xmlQuietly()
            ).node()
        );
    }

    /**
     * Constructor.
     *
     * @param xml Raw XMIR.
     */
    public XmlProgram(final XML xml) {
        this(xml.node());
    }

    /**
     * Constructor.
     *
     * @param root Root node.
     */
    public XmlProgram(final Node root) {
        this.root = root;
    }

    /**
     * Find top-level class.
     *
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
     * Convert to XML.
     *
     * @return XML representation of XmlProgram.
     */
    public XML toXml() {
        return new XMLDocument(this.root);
    }

    /**
     * Copy program with replaced top class.
     *
     * @param clazz Class to replace.
     * @return Program with replaced top class.
     */
    public XmlProgram replaceTopClass(final XmlClass clazz) {
        return new XmlProgram(
            new Xembler(
                new Directives()
                    .xpath("/program/objects")
                    .add("o")
                    .append(Directives.copyOf(clazz.toXml().node()))
                    .up()
            ).applyQuietly(this.withoutTopClass().root)
        );
    }

    /**
     * Copy program without top class.
     *
     * @return Program without top class.
     */
    @Deprecated
    public XmlProgram withoutTopClass() {
        final String name = this.top().bytecode(this.pckg(), true).name();
        return new XmlProgram(
            new Xembler(
                new Directives()
                    .xpath(String.format("/program/objects/o[@name='%s']", name))
                    .remove()
            ).applyQuietly(this.root)
        );
    }

    @Override
    public String toString() {
        return new XMLDocument(this.root).toString();
    }

    /**
     * Retrieve program package.
     * In case if metas are empty, or there is no package meta, or there is no tail, return empty
     * string.
     *
     * @return Package.
     */
    String pckg() {
        return new XmlNode(this.root)
            .xpath("/program/metas/meta[head='package']/tail/text()")
            .stream()
            .findFirst()
            .orElse("");
    }
}
