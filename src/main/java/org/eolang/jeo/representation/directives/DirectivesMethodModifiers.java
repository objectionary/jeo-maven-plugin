/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;

/**
 * Method modifiers.
 * Similar to {@link DirectivesClassModifiers} but for method-level modifiers.
 * @since 0.14.0
 */
public final class DirectivesMethodModifiers implements Iterable<Directive> {

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
    DirectivesMethodModifiers(final Format format, final int modifiers) {
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
            new DirectivesValue(this.format, "static", all.is(Opcodes.ACC_STATIC)),
            new DirectivesValue(this.format, "final", all.is(Opcodes.ACC_FINAL)),
            new DirectivesValue(this.format, "synchronized", all.is(Opcodes.ACC_SYNCHRONIZED)),
            new DirectivesValue(this.format, "bridge", all.is(Opcodes.ACC_BRIDGE)),
            new DirectivesValue(this.format, "varargs", all.is(Opcodes.ACC_VARARGS)),
            new DirectivesValue(this.format, "native", all.is(Opcodes.ACC_NATIVE)),
            new DirectivesValue(this.format, "abstract", all.is(Opcodes.ACC_ABSTRACT)),
            new DirectivesValue(this.format, "strict", all.is(Opcodes.ACC_STRICT)),
            new DirectivesValue(this.format, "synthetic", all.is(Opcodes.ACC_SYNTHETIC)),
            new DirectivesValue(this.format, "mandated", all.is(Opcodes.ACC_MANDATED))
        ).iterator();
    }
}
