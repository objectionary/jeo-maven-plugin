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
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Collection;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

/**
 * Bytecode field.
 * @since 0.2
 */
public final class BytecodeField {

    /**
     * Field name.
     */
    private final String name;

    /**
     * Descriptor.
     */
    private final String descriptor;

    /**
     * Signature.
     */
    private final String signature;

    /**
     * Set value.
     */
    private final Object value;

    /**
     * Access.
     */
    private final int access;

    /**
     * Annotations.
     */
    private final Collection<BytecodeAnnotation> annotations;

    /**
     * Constructor.
     * @param name Name.
     * @param descr Descriptor.
     * @param signature Signature.
     * @param value Value.
     * @param access Access.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public BytecodeField(
        final String name,
        final String descr,
        final String signature,
        final Object value,
        final int access
    ) {
        this.name = name;
        this.descriptor = descr;
        this.signature = signature;
        this.value = value;
        this.access = access;
        this.annotations = new ArrayList<>(0);
    }

    /**
     * Write field to a class.
     * @param visitor Visitor.
     */
    public void write(final ClassVisitor visitor) {
        final FieldVisitor fvisitor = visitor.visitField(
            this.access,
            this.name,
            this.descriptor,
            this.signature,
            this.value
        );
        this.annotations.forEach(annotation -> annotation.write(fvisitor));
    }

    /**
     * Add annotation.
     * @param descr Descriptor.
     * @param visible Visible.
     */
    public void withAnnotation(final String descr, final boolean visible) {
        this.annotations.add(new BytecodeAnnotation(descr, visible));
    }
}
