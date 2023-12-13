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
package org.eolang.jeo.representation;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import org.eolang.jeo.Details;
import org.eolang.jeo.Representation;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.xmir.XmlBytecode;
import org.eolang.parser.Schema;

/**
 * Intermediate representation of a class files from XMIR.
 *
 * @since 0.1.0
 */
public final class EoRepresentation implements Representation {

    /**
     * XML.
     */
    private final XML xml;

    /**
     * Source of the XML.
     */
    private final String source;

    /**
     * Constructor.
     * @param path Path to XML file.
     */
    public EoRepresentation(final Path path) {
        this(EoRepresentation.open(path), path.getFileName().toString());
    }

    /**
     * Constructor.
     * @param lines Xml document lines.
     */
    public EoRepresentation(final String... lines) {
        this(new XMLDocument(String.join("\n", lines)));
    }

    /**
     * Constructor.
     * @param xml XML.
     */
    public EoRepresentation(final XML xml) {
        this(xml, "Unknown");
    }

    /**
     * Constructor.
     * @param xml XML.
     * @param source Source of the XML.
     */
    public EoRepresentation(
        final XML xml,
        final String source
    ) {
        this.xml = xml;
        this.source = source;
    }

    @Override
    public Details details() {
        return new Details(this.className(), this.source);
    }

    @Override
    public String name() {
        return this.className();
    }

    @Override
    public XML toEO() {
        return this.xml;
    }

    @Override
    public Bytecode toBytecode() {
        new Schema(this.xml).check();
        return new XmlBytecode(this.xml).bytecode();
    }

    /**
     * Retrieves class name from XMIR.
     * @return Class name.
     */
    private String className() {
        return new ClassName(
            this.xml.xpath("/program/metas/meta/tail/text()").stream().findFirst().orElse(""),
            this.xml.xpath("/program/@name").get(0)
        ).full();
    }

    /**
     * Convert path to XML.
     * @param path Path to XML file.
     * @return XML.
     */
    private static XML open(final Path path) {
        try {
            return new XMLDocument(path.toFile());
        } catch (final FileNotFoundException exception) {
            throw new IllegalStateException(
                String.format("Can't find file '%s'", path),
                exception
            );
        }
    }
}
