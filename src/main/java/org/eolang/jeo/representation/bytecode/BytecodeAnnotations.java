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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesAnnotations;
import org.objectweb.asm.MethodVisitor;

/**
 * Bytecode annotations.
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
public final class BytecodeAnnotations {
    /**
     * All annotations.
     */
    private final List<BytecodeAnnotation> all;

    /**
     * Constructor.
     * @param all All annotations.
     */
    public BytecodeAnnotations(final BytecodeAnnotation... all) {
        this(Arrays.asList(all));
    }

    /**
     * Constructor.
     * @param all All annotations.
     */
    public BytecodeAnnotations(final Stream<BytecodeAnnotation> all) {
        this(all.collect(Collectors.toList()));
    }

    /**
     * Constructor.
     * @param all All annotations.
     */
    public BytecodeAnnotations(final List<BytecodeAnnotation> all) {
        this.all = all;
    }

    /**
     * Write annotations to the ASM method visitor.
     * @param visitor Method visitor.
     */
    public void write(final MethodVisitor visitor) {
        this.all.forEach(annotation -> annotation.write(visitor));
    }

    /**
     * Write annotations to the custom class writer.
     * @param visitor Custom class writer.
     */
    public void write(final CustomClassWriter visitor) {
        this.all.forEach(annotation -> annotation.write(visitor));
    }

    /**
     * Write the parameter.
     * @param index Index of the parameter.
     * @param writer Method visitor.
     */
    void write(final int index, final MethodVisitor writer) {
        this.all.forEach(annotation -> annotation.write(index, writer));
    }


    /**
     * All annotations.
     * @return Annotations.
     */
    public List<BytecodeAnnotation> annotations() {
        return Collections.unmodifiableList(this.all);
    }

    public DirectivesAnnotations directives(final String name) {
        return new DirectivesAnnotations(
            this.all.stream()
                .map(BytecodeAnnotation::directives)
                .collect(Collectors.toList()),
            name
        );
    }

    public DirectivesAnnotations directives() {
        return this.directives("annotations");
    }
}
