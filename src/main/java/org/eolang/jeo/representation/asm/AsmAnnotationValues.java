/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotationValue;

/**
 * Annotation values in ASM representation.
 * @since 0.15.0
 */
public final class AsmAnnotationValues {
    /**
     * Raw values list.
     */
    private final List<Object> values;

    /**
     * Constructor.
     * @param values Raw values list.
     */
    AsmAnnotationValues(final List<Object> values) {
        this.values = values;
    }

    /**
     * Convert to bytecode representation.
     * @return Bytecode representation.
     */
    public List<BytecodeAnnotationValue> bytecode() {
        final List<BytecodeAnnotationValue> properties = new ArrayList<>(0);
        final List<Object> all = Optional.ofNullable(this.values).orElse(new ArrayList<>(0));
        for (int index = 0; index < all.size(); index += 2) {
            properties.add(
                new AsmAnnotationProperty(
                    (String) all.get(index),
                    all.get(index + 1)
                ).bytecode()
            );
        }
        return properties;
    }
}
