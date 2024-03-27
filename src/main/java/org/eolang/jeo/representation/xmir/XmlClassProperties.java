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

import com.jcabi.xml.XMLDocument;
import java.util.List;
import java.util.Optional;
import org.eolang.jeo.representation.DefaultVersion;
import org.eolang.jeo.representation.bytecode.BytecodeClassProperties;

/**
 * XML representation of a class.
 *
 * @since 0.1.0
 */
public final class XmlClassProperties {

    /**
     * XML representation of a class.
     */
    private final XMLDocument clazz;

    /**
     * Constructor.
     * @param clazz XMl representation of a class.
     */
    public XmlClassProperties(final XmlNode clazz) {
        this(clazz.asDocument());
    }

    /**
     * Constructor.
     * @param clazz XML representation of a class.
     */
    public XmlClassProperties(final XMLDocument clazz) {
        this.clazz = clazz;
    }

    /**
     * Retrieve bytecode 'version'.
     * @return Bytecode version.
     */
    int version() {
        final List<String> version = this.clazz.xpath("./o[@name='version']/text()");
        final int result;
        if (version.isEmpty()) {
            result = new DefaultVersion().bytecode();
        } else {
            result = new HexString(version.get(0)).decodeAsInt();
        }
        return result;
    }

    /**
     * Retrieve 'access' modifiers of a class.
     * @return Access modifiers.
     */
    int access() {
        return new HexString(this.clazz.xpath("./o[@name='access']/text()").get(0)).decodeAsInt();
    }

    /**
     * Retrieve 'signature' of a class.
     * @return Signature.
     */
    Optional<String> signature() {
        return this.clazz.xpath("./o[@name='signature']/text()")
            .stream()
            .map(HexString::new)
            .map(HexString::decode)
            .findFirst();
    }

    /**
     * Retrieve 'supername' of a class.
     * @return Supername.
     */
    String supername() {
        return this.clazz.xpath("./o[@name='supername']/text()")
            .stream()
            .map(HexString::new)
            .map(HexString::decode)
            .findFirst().orElse("java/lang/Object");
    }

    /**
     * Retrieve 'interfaces' of a class.
     * @return Interfaces.
     */
    String[] interfaces() {
        return this.clazz.xpath("./o[@name='interfaces']/o/text()")
            .stream()
            .map(HexString::new)
            .map(HexString::decode).toArray(String[]::new);
    }

    /**
     * Convert to bytecode properties.
     * @return Bytecode properties.
     */
    BytecodeClassProperties toBytecodeProperties() {
        return new BytecodeClassProperties(
            this.version(),
            this.access(),
            this.signature().orElse(null),
            this.supername(),
            this.interfaces()
        );
    }
}

