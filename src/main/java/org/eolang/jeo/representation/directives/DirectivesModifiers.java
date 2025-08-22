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
     * Access modifiers.
     */
    private final int modifiers;

    /**
     * Constructor.
     * @param modifiers Access modifiers.
     */
    DirectivesModifiers(final int modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "modifiers",
            "modifiers",
            new DirectivesValue("public", this.is(Opcodes.ACC_PUBLIC)),
            new DirectivesValue("private", this.is(Opcodes.ACC_PRIVATE)),
            new DirectivesValue("protected", this.is(Opcodes.ACC_PROTECTED)),
            new DirectivesValue("static", this.is(Opcodes.ACC_STATIC)),
            new DirectivesValue("final", this.is(Opcodes.ACC_FINAL)),
            new DirectivesValue("synchronized", this.is(Opcodes.ACC_SYNCHRONIZED)),
            new DirectivesValue("bridge", this.is(Opcodes.ACC_BRIDGE)),
            new DirectivesValue("varargs", this.is(Opcodes.ACC_VARARGS)),
            new DirectivesValue("native", this.is(Opcodes.ACC_NATIVE)),
            new DirectivesValue("abstract", this.is(Opcodes.ACC_ABSTRACT)),
            new DirectivesValue("strict", this.is(Opcodes.ACC_STRICT)),
            new DirectivesValue("synthetic", this.is(Opcodes.ACC_SYNTHETIC)),
            new DirectivesValue("mandated", this.is(Opcodes.ACC_MANDATED))
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
