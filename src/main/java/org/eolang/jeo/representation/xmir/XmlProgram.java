/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
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
import org.eolang.jeo.representation.PrefixedName;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;

/**
 * XMIR Program.
 * @since 0.1
 */
public final class XmlProgram {

    /**
     * Root node.
     */
    private final XmlNode root;

    /**
     * Constructor.
     * @param lines Xmir lines.
     */
    public XmlProgram(final String... lines) {
        this(new JcabiXmlNode(lines));
    }

    /**
     * Constructor.
     * @param xml Raw XMIR.
     */
    public XmlProgram(final XML xml) {
        this(new JcabiXmlDoc(xml).root());
    }

    /**
     * Constructor.
     * @param root Root node.
     */
    public XmlProgram(final XmlNode root) {
        this.root = root;
    }

    @Override
    public String toString() {
        return this.root.toString();
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
        return new XmlClass(this.root.child("objects").child("o"));
    }

    /**
     * Retrieve program package.
     * In case if metas are empty, or there is no package meta, or there is no tail, return empty
     * string.
     *
     * @return Package.
     */
    private String pckg() {
        return this.root
            .xpath("/program/metas/meta[head='package']/tail/text()")
            .stream()
            .findFirst()
            .map(PrefixedName::new)
            .map(PrefixedName::decode)
            .orElse("");
    }
}
