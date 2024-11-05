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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesAttributes;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Bytecode attributes.
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
public final class BytecodeAttributes {

    /**
     * All attributes.
     */
    private final List<BytecodeAttribute> all;

    /**
     * Constructor.
     * @param all All attributes.
     */
    public BytecodeAttributes(final BytecodeAttribute... all) {
        this(Arrays.asList(all));
    }

    /**
     * Constructor.
     * @param all All attributes.
     */
    public BytecodeAttributes(final List<BytecodeAttribute> all) {
        this.all = all;
    }

    /**
     * Convert to directives.
     * @param name Name of the attributes in EO representation.
     * @return Directives.
     */
    public DirectivesAttributes directives(final String name) {
        return new DirectivesAttributes(
            name,
            this.all.stream().map(BytecodeAttribute::directives).collect(Collectors.toList())
        );
    }

    /**
     * Write to class.
     * @param clazz Bytecode where to write.
     */
    void write(final ClassVisitor clazz) {
        this.all.forEach(attr -> attr.write(clazz));
    }

    /**
     * Write to method.
     * @param method Bytecode where to write.
     */
    void write(final MethodVisitor method) {
        this.all.forEach(attr -> attr.write(method));
    }
}
