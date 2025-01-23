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

import java.util.Optional;
import org.eolang.jeo.representation.DefaultVersion;
import org.eolang.jeo.representation.bytecode.BytecodeClassProperties;

/**
 * XML representation of a class.
 * @since 0.1.0
 */
public final class XmlClassProperties {

    /**
     * XML representation of a class.
     */
    private final XmlNode clazz;

    /**
     * Constructor.
     * @param xmlclass XMl representation of a class.
     */
    XmlClassProperties(final XmlNode xmlclass) {
        this.clazz = xmlclass;
    }

    /**
     * Convert to bytecode properties.
     * @return Bytecode properties.
     */
    public BytecodeClassProperties bytecode() {
        try {
            return new BytecodeClassProperties(
                this.version(),
                this.access(),
                this.signature(),
                this.supername(),
                this.interfaces()
            );
        } catch (final IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                String.format("Invalid class properties: %s", this.clazz),
                exception
            );
        }
    }

    /**
     * Retrieve 'access' modifiers of a class.
     * @return Access modifiers.
     */
    private int access() {
        return (int) new XmlValue(this.clazz.child("as", "access")).object();
    }

    /**
     * Retrieve 'signature' of a class.
     * @return Signature.
     */
    private String signature() {
        return this.child("signature")
            .map(XmlValue::new)
            .map(XmlValue::string)
            .filter(s -> !s.isEmpty())
            .orElse(null);
    }

    /**
     * Retrieve 'supername' of a class.
     * @return Supername.
     */
    private String supername() {
        return this.child("supername")
            .map(XmlValue::new)
            .map(XmlValue::string)
            .filter(s -> !s.isEmpty())
            .orElse("java/lang/Object");
    }

    /**
     * Retrieve 'interfaces' of a class.
     * @return Interfaces.
     */
    private String[] interfaces() {
        return this.child("interfaces")
            .map(
                node -> node.children()
                    .map(XmlValue::new)
                    .map(XmlValue::string)
                    .toArray(String[]::new)
            ).orElse(new String[0]);
    }

    /**
     * Retrieve bytecode 'version'.
     * @return Bytecode version.
     */
    private int version() {
        return this.child("version")
            .map(XmlValue::new)
            .map(XmlValue::object)
            .map(Integer.class::cast)
            .orElse(new DefaultVersion().bytecode());
    }

    /**
     * Retrieve child node by name.
     * @param name Name of the child node.
     * @return Child node.
     */
    private Optional<XmlNode> child(final String name) {
        return this.clazz.optchild("as", name);
    }

}
