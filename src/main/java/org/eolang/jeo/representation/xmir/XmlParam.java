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

import org.eolang.jeo.representation.EncodedString;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameter;
import org.objectweb.asm.Type;

/**
 * Xmir representation of a method parameter.
 * @since 0.4
 */
public final class XmlParam {

    /**
     * Root node from which we will get all required data.
     */
    private final XmlNode root;

    /**
     * Constructor.
     * @param root Parameter xml node.
     */
    public XmlParam(final XmlNode root) {
        this.root = root;
    }

    /**
     * Convert to bytecode.
     * @return Bytecode method parameter.
     */
    public BytecodeMethodParameter bytecode() {
        return new BytecodeMethodParameter(
            this.index(),
            this.pure(),
            this.access(),
            this.type(),
            this.annotations()
        );
    }

    /**
     * Type of the parameter.
     * @return Type.
     */
    private Type type() {
        return Type.getType(new EncodedString(this.suffix(1)).decode());
    }

    /**
     * Pure name of the parameter.
     * @return Name.
     */
    private String pure() {
        return this.suffix(2);
    }

    /**
     * Access modifier of the parameter.
     * @return Access.
     */
    private int access() {
        return Integer.parseInt(this.suffix(3));
    }

    /**
     * Index of the parameter in the method.
     * @return Index.
     */
    private int index() {
        return Integer.parseInt(this.suffix(4));
    }

    /**
     * Annotations of the parameter.
     * @return Annotations.
     */
    private BytecodeAnnotations annotations() {
        return this.root.children()
            .filter(
                node -> node.attribute("name")
                    .map(name -> name.startsWith("param-annotations-"))
                    .orElse(false)
            )
            .findFirst()
            .map(XmlAnnotations::new)
            .map(XmlAnnotations::bytecode)
            .orElse(new BytecodeAnnotations());
    }

    /**
     * Get the suffix of the name attribute.
     * position[0]-position[1]-position[2].
     * param      -type       -index
     * @param position Position of the suffix.
     * @return Suffix.
     */
    private String suffix(final int position) {
        return this.name().split("-")[position];
    }

    /**
     * Name attribute of the parameter.
     * @return Name attribute.
     */
    private String name() {
        return this.root.attribute("name").orElseThrow(
            () -> new IllegalStateException(
                String.format("'name' attribute is not present in xml param %n%s%n", this.root)
            )
        );
    }
}
