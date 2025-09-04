/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesTypeAnnotations;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.RecordComponentVisitor;

/**
 * Bytecode type annotations.
 * @since 0.15.0
 */
@ToString
@EqualsAndHashCode
public final class BytecodeTypeAnnotations {

    /**
     * All type annotations.
     */
    private final List<BytecodeTypeAnnotation> annotations;

    /**
     * Constructor.
     */
    public BytecodeTypeAnnotations() {
        this(Collections.emptyList());
    }

    /**
     * Constructor.
     * @param annotations All type annotations.
     */
    public BytecodeTypeAnnotations(final BytecodeTypeAnnotation... annotations) {
        this(Arrays.asList(annotations));
    }

    /**
     * Constructor.
     * @param annotations All type annotations.
     */
    public BytecodeTypeAnnotations(final List<BytecodeTypeAnnotation> annotations) {
        this.annotations = Optional.ofNullable(annotations).orElse(Collections.emptyList());
    }

    /**
     * Write to visitor.
     * @param visitor Visitor to write to.
     */
    public void write(final RecordComponentVisitor visitor) {
        this.annotations.forEach(annotation -> annotation.write(visitor));
    }

    /**
     * Convert to directives.
     * @param format Format of the directives.
     * @return Directives with the name "annotations".
     */
    public DirectivesTypeAnnotations directives(final Format format) {
        final AtomicInteger index = new AtomicInteger(0);
        return new DirectivesTypeAnnotations(
            this.annotations.stream()
                .map(ann -> ann.directives(index.getAndIncrement(), format))
                .collect(Collectors.toList())
        );
    }
}
