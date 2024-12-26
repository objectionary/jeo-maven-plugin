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

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.PrefixedName;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;
import org.eolang.jeo.representation.directives.DirectivesClass;
import org.eolang.jeo.representation.directives.DirectivesMetas;
import org.eolang.jeo.representation.directives.DirectivesProgram;
import org.w3c.dom.Node;
import org.xembly.Xembler;

/**
 * XMIR Program.
 *
 * @since 0.1
 */
public final class XmlProgram {

    /**
     * Root node.
     * Here we use the {@link Node} class instead of the {@link com.jcabi.xml.XML}
     * by performance reasons.
     * In some cases {@link Node} 10 times faster than {@link com.jcabi.xml.XML}.
     * You can read more about it here:
     * <a href="https://github.com/objectionary/jeo-maven-plugin/pull/924">Optimization</a>
     */
    private final Node root;

    /**
     * Constructor.
     * @param lines Xmir lines.
     */
    public XmlProgram(final String... lines) {
        this(new XMLDocument(String.join("\n", lines)));
    }

    /**
     * Constructor.
     *
     * @param xml Raw XMIR.
     */
    public XmlProgram(final XML xml) {
        this(xml.inner().getFirstChild());
    }

    /**
     * Constructor.
     *
     * @param name Class name.
     */
    XmlProgram(final ClassName name) {
        this(
            new XMLDocument(
                new Xembler(
                    new DirectivesProgram(
                        new DirectivesClass(name), new DirectivesMetas(name)
                    )
                ).xmlQuietly()
            )
        );
    }

    /**
     * Constructor.
     *
     * @param root Root node.
     */
    public XmlProgram(final Node root) {
        this.root = root;
    }

    @Override
    public String toString() {
        return new XMLDocument(this.root).toString();
    }

    /**
     * Convert to bytecode.
     * @return Bytecode program.
     */
    public BytecodeProgram bytecode() {
        try {
            return new BytecodeProgram(this.pckg(), this.top().bytecode());
        } catch (final IllegalStateException exception) {
            throw new ParsingException(
                String.format(
                    "Unexpected exception during parsing the program in package '%s'",
                    this.pckg()
                ),
                exception
            );
        }
    }

    /**
     * Find top-level class.
     *
     * @return Class.
     */
    private XmlClass top() {
        return new XmlNode(this.root)
            .child("objects")
            .child("o")
            .toClass();
    }

    /**
     * Retrieve program package.
     * In case if metas are empty, or there is no package meta, or there is no tail, return empty
     * string.
     *
     * @return Package.
     */
    private String pckg() {
        return new XmlNode(this.root)
            .xpath("/program/metas/meta[head='package']/tail/text()")
            .stream()
            .findFirst()
            .map(PrefixedName::new)
            .map(PrefixedName::decode)
            .orElse("");
    }
}
