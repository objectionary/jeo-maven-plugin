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
package org.eolang.jeo.representation;

import com.jcabi.xml.XML;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.UncheckedInput;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Synced;
import org.cactoos.scalar.Unchecked;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.xmir.XmlProgram;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Intermediate representation of a class files from XMIR.
 *
 * @since 0.1.0
 */
public final class XmirRepresentation {

    /**
     * XPath's factory.
     */
    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();

    /**
     * XML document factory.
     */
    private static final DocumentBuilderFactory DOC_FACTORY = DocumentBuilderFactory.newInstance();

    /**
     * XML.
     */
    private final Unchecked<Node> xml;

    /**
     * Source of the XML.
     */
    private final String source;

    /**
     * Constructor.
     * @param path Path to XML file.
     */
    public XmirRepresentation(final Path path) {
        this(XmirRepresentation.fromFile(path), path.toAbsolutePath().toString());
    }

    /**
     * Constructor.
     * @param xml XML.
     */
    public XmirRepresentation(final XML xml) {
        this(xml.node().getFirstChild(), "Unknown");
    }

    /**
     * Constructor.
     * @param xml XML.
     * @param source Source of the XML.
     */
    private XmirRepresentation(
        final Node xml,
        final String source
    ) {
        this(new Unchecked<>(() -> xml), source);
    }

    /**
     * Constructor.
     * @param xml XML source.
     * @param source Source of the XML.
     */
    private XmirRepresentation(
        final Unchecked<Node> xml,
        final String source
    ) {
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
        final Node node = this.xml.value();
        final XPath xpath = XmirRepresentation.XPATH_FACTORY.newXPath();
        try {
            return new ClassName(
                Optional.ofNullable(
                    ((Node) xpath.evaluate(
                        "/program/metas/meta/tail/text()",
                        node,
                        XPathConstants.NODE
                    )).getTextContent()
                ).orElse(""),
                String.valueOf(
                    xpath.evaluate(
                        "/program/@name",
                        node,
                        XPathConstants.STRING
                    )
                )
            ).full();
        } catch (final XPathExpressionException exception) {
            throw new IllegalStateException(
                String.format("Can't extract class name from the '%s' source", this.source),
                exception
            );
        }
    }

    /**
     * Convert to bytecode.
     * @return Array of bytes.
     */
    public Bytecode toBytecode() {
        final Node xmir = this.xml.value();
        try {
            new OptimizedSchema(xmir).check();
            return new XmlProgram(xmir).bytecode().bytecode();
        } catch (final IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                String.format("Can't transform '%s' to bytecode", xmir),
                exception
            );
        } catch (final IllegalStateException exception) {
            throw new IllegalStateException(
                String.format("Can't transform XMIR to bytecode from the '%s' source", this.source),
                exception
            );
        }
    }

    /**
     * Prestructor that converts a path to a lazy XML.
     * @param path Path to an XML file.
     * @return Lazy XML.
     */
    private static Unchecked<Node> fromFile(final Path path) {
        return new Unchecked<>(new Synced<>(new Sticky<>(() -> XmirRepresentation.open(path))));
    }

    /**
     * Convert a path to XML.
     * @param path Path to XML file.
     * @return XML.
     * @checkstyle IllegalCatchCheck (20 lines)
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private static Node open(final Path path) {
        try {
            return XmirRepresentation.DOC_FACTORY
                .newDocumentBuilder()
                .parse(path.toFile())
                .getDocumentElement();
        } catch (final FileNotFoundException exception) {
            throw new IllegalStateException(
                String.format("Can't find file '%s'", path),
                exception
            );
        } catch (final Exception broken) {
            throw new IllegalStateException(
                String.format(
                    "Can't parse XML from the file '%s'",
                    path
                ),
                broken
            );
        }
    }

    /**
     * Optimized schema for XMIR.
     * @since 0.6
     */
    private static class OptimizedSchema {
        /**
         * Node.
         */
        private final Node node;

        /**
         * Schema factory.
         */
        private final SchemaFactory factory;

        /**
         * Constructor.
         * @param node Node.
         */
        OptimizedSchema(final Node node) {
            this(node, SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema"));
        }

        /**
         * Constructor.
         * @param node Node.
         * @param factory Schema factory.
         */
        private OptimizedSchema(final Node node, final SchemaFactory factory) {
            this.node = node;
            this.factory = factory;
        }

        /**
         * Check the node.
         */
        void check() {
            try {
                this.factory.newSchema(
                    new StreamSource(new UncheckedInput(new ResourceOf("XMIR.xsd")).stream())
                ).newValidator().validate(new DOMSource(this.node));
            } catch (final IOException | SAXException exception) {
                throw new IllegalStateException(
                    String.format(
                        "There are XSD violations, see the log",
                        exception.getMessage()
                    ),
                    exception
                );
            }
        }
    }
}
