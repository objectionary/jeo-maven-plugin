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
package org.eolang.jeo.representation.bytecode;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesMethodParam;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.xembly.Directive;

/**
 * Bytecode method parameter.
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
public final class BytecodeMethodParameter {

    /**
     * Index of the parameter.
     */
    private final int index;

    /**
     * Name of the parameter.
     */
    private final String name;

    /**
     * Type of the parameter.
     */
    private final Type type;

    /**
     * Annotations of the parameter.
     */
    private final BytecodeAnnotations annotations;

    /**
     * Constructor.
     * @param index Index of the parameter.
     * @param type Type of the parameter.
     */
    public BytecodeMethodParameter(final int index, final Type type) {
        this(index, type, new BytecodeAnnotations());
    }

    /**
     * Constructor.
     * @param index Index of the parameter.
     * @param type Type of the parameter.
     * @param annotations Annotations of the parameter.
     */
    public BytecodeMethodParameter(
        final int index,
        final Type type,
        final BytecodeAnnotations annotations
    ) {
        this(index, String.format("arg%d", index), type, annotations);
    }

    /**
     * Constructor.
     * @param index Index of the parameter.
     * @param name Name of the parameter.
     * @param type Type of the parameter.
     * @param annotations Annotations of the parameter.
     */
    public BytecodeMethodParameter(
        final int index,
        final String name,
        final Type type,
        final BytecodeAnnotations annotations
    ) {
        this.index = index;
        this.name = name;
        this.type = type;
        this.annotations = annotations;
    }

    /**
     * Write to the method visitor.
     * @param visitor Method visitor.
     */
    public void write(final MethodVisitor visitor) {
        this.annotations.write(this.index, visitor);
    }

    /**
     * Convert to directives.
     * @return Directives.
     */
    public Iterable<Directive> directives() {
        return new DirectivesMethodParam(
            this.index,
            this.name,
            this.type,
            this.annotations.directives(String.format("param-annotations-%d", this.index))
        );
    }
}
