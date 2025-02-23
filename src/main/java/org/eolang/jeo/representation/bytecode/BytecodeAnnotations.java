/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
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
     * All annotations.
     * @return Annotations.
     */
    public List<BytecodeAnnotation> annotations() {
        return Collections.unmodifiableList(this.all);
    }

    /**
     * Directives with the given name.
     * @param name Name of the directives.
     * @return Directives.
     */
    public DirectivesAnnotations directives(final String name) {
        return new DirectivesAnnotations(
            this.all.stream()
                .map(BytecodeAnnotation::directives)
                .collect(Collectors.toList()),
            name
        );
    }

    /**
     * Directives with the name "annotations".
     * @return Directives.
     */
    public DirectivesAnnotations directives() {
        return this.directives("annotations");
    }

    /**
     * Write annotations to the ASM method visitor.
     * @param visitor Method visitor.
     */
    void write(final MethodVisitor visitor) {
        this.all.forEach(annotation -> annotation.write(visitor));
    }

    /**
     * Write annotations to the custom class writer.
     * @param visitor Custom class writer.
     */
    void write(final CustomClassWriter visitor) {
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
}
