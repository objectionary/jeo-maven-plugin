/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.directives.DirectivesTypeAnnotations;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.RecordComponentVisitor;

/**
 * Bytecode type annotations.
 *
 * @since 0.15.0
 */
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
     *
     * @param annotations All type annotations
     */
    public BytecodeTypeAnnotations(final BytecodeTypeAnnotation... annotations) {
        this(Arrays.asList(annotations));
    }

    /**
     * Constructor.
     *
     * @param annotations All type annotations
     */
    public BytecodeTypeAnnotations(final List<BytecodeTypeAnnotation> annotations) {
        this.annotations = annotations;
    }

    /**
     * Write to visitor.
     *
     * @param visitor Visitor to write to
     */
    public void write(final RecordComponentVisitor visitor) {
        this.present().forEach(annotation -> annotation.write(visitor));
    }

    /**
     * Convert to directives.
     *
     * @param format Format of the directives
     * @return Directives with the name "annotations"
     */
    public DirectivesTypeAnnotations directives(final Format format) {
        final AtomicInteger index = new AtomicInteger(0);
        return new DirectivesTypeAnnotations(
            this.present().stream()
                .map(ann -> ann.directives(index.getAndIncrement(), format))
                .collect(Collectors.toList())
        );
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeTypeAnnotations) {
            result = Objects.equals(
                this.present(), ((BytecodeTypeAnnotations) other).present()
            );
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.present());
    }

    @Override
    public String toString() {
        return String.format("BytecodeTypeAnnotations(annotations=%s)", this.present());
    }

    private List<BytecodeTypeAnnotation> present() {
        final List<BytecodeTypeAnnotation> result;
        if (this.annotations == null) {
            result = Collections.emptyList();
        } else {
            result = this.annotations;
        }
        return result;
    }
}
