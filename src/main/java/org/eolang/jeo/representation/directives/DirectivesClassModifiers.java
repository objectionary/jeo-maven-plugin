/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;

/**
 * Directives for class modifiers.
 * Similar to {@link DirectivesMethodModifiers} but for class-level modifiers.
 * @since 0.15.0
 */
public final class DirectivesClassModifiers implements Iterable<Directive> {

    /**
     * Format of the directives.
     */
    private final Format format;

    /**
     * Access modifiers.
     */
    private final int modifiers;

    /**
     * Constructor.
     * @param format Format of the directives.
     * @param modifiers Access modifiers.
     */
    DirectivesClassModifiers(final Format format, final int modifiers) {
        this.format = format;
        this.modifiers = modifiers;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Modifiers all = new Modifiers(this.modifiers);
        return new DirectivesJeoObject(
            "modifiers",
            "modifiers",
            new DirectivesValue(this.format, "public", all.is(Opcodes.ACC_PUBLIC)),
            new DirectivesValue(this.format, "private", all.is(Opcodes.ACC_PRIVATE)),
            new DirectivesValue(this.format, "protected", all.is(Opcodes.ACC_PROTECTED)),
            new DirectivesValue(this.format, "final", all.is(Opcodes.ACC_FINAL)),
            new DirectivesValue(this.format, "super", all.is(Opcodes.ACC_SUPER)),
            new DirectivesValue(this.format, "interface", all.is(Opcodes.ACC_INTERFACE)),
            new DirectivesValue(this.format, "abstract", all.is(Opcodes.ACC_ABSTRACT)),
            new DirectivesValue(this.format, "synthetic", all.is(Opcodes.ACC_SYNTHETIC)),
            new DirectivesValue(this.format, "annotation", all.is(Opcodes.ACC_ANNOTATION)),
            new DirectivesValue(this.format, "enum", all.is(Opcodes.ACC_ENUM)),
            new DirectivesValue(this.format, "mandated", all.is(Opcodes.ACC_MANDATED)),
            new DirectivesValue(this.format, "module", all.is(Opcodes.ACC_MODULE)),
            new DirectivesValue(this.format, "record", all.is(Opcodes.ACC_RECORD)),
            new DirectivesValue(this.format, "deprecated", all.is(Opcodes.ACC_DEPRECATED))
        ).iterator();
    }
}
