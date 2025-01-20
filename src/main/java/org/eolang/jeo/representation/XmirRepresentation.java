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
package org.eolang.jeo.representation;

import com.jcabi.xml.XML;
import java.nio.file.Path;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.xmir.JcabiXmlDoc;
import org.eolang.jeo.representation.xmir.XmlDoc;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.jeo.representation.xmir.XmlProgram;

/**
 * Intermediate representation of a class files from XMIR.
 *
 * @since 0.1.0
 */
public final class XmirRepresentation {

    /**
     * XML.
     */
    private final XmlDoc xml;

    /**
     * Source of the XML.
     */
    private final String source;

    /**
     * Constructor.
     * @param path Path to XML file.
     */
    public XmirRepresentation(final Path path) {
        this(new JcabiXmlDoc(path), path.toAbsolutePath().toString());
    }

    /**
     * Constructor.
     * @param xml XML.
     */
    public XmirRepresentation(final XML xml) {
        this(new JcabiXmlDoc(xml), "Unknown");
    }

    /**
     * Constructor.
     * @param xml XML.
     * @param source Source of the XML.
     */
    private XmirRepresentation(final XmlDoc xml, final String source) {
        this.xml = xml;
        this.source = source;
    }

    /**
     * Retrieves class name from XMIR.
     * This method intentionally uses classes from `org.w3c.dom` instead of `com.jcabi.xml`
     * by performance reasons.
     * @return Class name.
     */
    public String name() {
        final XmlNode root = this.xml.root();
        return new ClassName(
            root.xpath("/program/metas/meta[head[text()]='package']/tail/text()")
                .stream()
                .findFirst()
                .orElse(""),
            root.xpath("/program/@name").get(0)
        ).full();
    }

    /**
     * Convert to bytecode.
     * @return Array of bytes.
     */
    public Bytecode toBytecode() {
        try {
            return new XmlProgram(this.xml.root()).bytecode().bytecode();
        } catch (final IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                String.format("Can't transform '%s' to bytecode", this.xml),
                exception
            );
        } catch (final IllegalStateException exception) {
            throw new IllegalStateException(
                String.format("Can't transform XMIR to bytecode from the '%s' source", this.source),
                exception
            );
        }
    }
}
