/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import com.jcabi.xml.XML;
import java.nio.file.Path;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.xmir.JcabiXmlDoc;
import org.eolang.jeo.representation.xmir.XmlDoc;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.jeo.representation.xmir.XmlObject;

/**
 * Intermediate representation of class files from XMIR format.
 *
 * <p>This class provides a unified interface for working with XMIR (EO XML representation)
 * files. It can read XMIR from files or XML objects and convert them to Java bytecode.</p>
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
     * @param path The path to XMIR file to read
     */
    public XmirRepresentation(final Path path) {
        this(new JcabiXmlDoc(path), path.toAbsolutePath().toString());
    }

    /**
     * Constructor.
     * @param xml The XML object containing XMIR representation
     */
    public XmirRepresentation(final XML xml) {
        this(new JcabiXmlDoc(xml), "Unknown");
    }

    /**
     * Constructor.
     * @param xml The XML document wrapper
     * @param source The source description for error reporting
     */
    private XmirRepresentation(final XmlDoc xml, final String source) {
        this.xml = xml;
        this.source = source;
    }

    /**
     * Retrieves class name from XMIR.
     * <p>This method intentionally uses classes from {@code org.w3c.dom} instead of
     * {@code com.jcabi.xml} for performance reasons.</p>
     * @return Fully qualified class name
     */
    public String name() {
        final XmlNode root = this.xml.root();
        return new ClassName(
            root.xpath("/object/metas/meta[head[text()]='package']/tail/text()")
                .stream()
                .findFirst()
                .orElse(""),
            root.xpath("/object/o/@name").get(0)
        ).full();
    }

    /**
     * Convert to bytecode.
     * @return Bytecode object containing the compiled class
     */
    public Bytecode toBytecode() {
        try {
            return new XmlObject(this.xml.root()).bytecode().bytecode();
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
