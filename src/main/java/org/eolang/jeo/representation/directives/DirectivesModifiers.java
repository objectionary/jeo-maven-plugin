/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;

/**
 * Method modifiers.
 * @since 0.14.0
 */
public final class DirectivesModifiers implements Iterable<Directive> {

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
    DirectivesModifiers(final Format format, final int modifiers) {
        this.format = format;
        this.modifiers = modifiers;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "modifiers",
            "modifiers",
            new DirectivesValue(this.format, "public", this.is(Opcodes.ACC_PUBLIC)),
            new DirectivesValue(this.format, "private", this.is(Opcodes.ACC_PRIVATE)),
            new DirectivesValue(this.format, "protected", this.is(Opcodes.ACC_PROTECTED)),
            new DirectivesValue(this.format, "static", this.is(Opcodes.ACC_STATIC)),
            new DirectivesValue(this.format, "final", this.is(Opcodes.ACC_FINAL)),
            new DirectivesValue(this.format, "synchronized", this.is(Opcodes.ACC_SYNCHRONIZED)),
            new DirectivesValue(this.format, "bridge", this.is(Opcodes.ACC_BRIDGE)),
            new DirectivesValue(this.format, "varargs", this.is(Opcodes.ACC_VARARGS)),
            new DirectivesValue(this.format, "native", this.is(Opcodes.ACC_NATIVE)),
            new DirectivesValue(this.format, "abstract", this.is(Opcodes.ACC_ABSTRACT)),
            new DirectivesValue(this.format, "strict", this.is(Opcodes.ACC_STRICT)),
            new DirectivesValue(this.format, "synthetic", this.is(Opcodes.ACC_SYNTHETIC)),
            new DirectivesValue(this.format, "mandated", this.is(Opcodes.ACC_MANDATED))
        ).iterator();
    }

    /**
     * Checks if the given flag is set.
     * @param flag Flag to check.
     * @return True if the flag is set, false otherwise.
     * @checkstyle MethodNameCheck (3 lines)
     */
    @SuppressWarnings("PMD.ShortMethodName")
    private boolean is(final int flag) {
        return (this.modifiers & flag) != 0;
    }
}
