/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotationAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeArrayAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeEnumAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodePlainAnnotationValue;
import org.objectweb.asm.tree.AnnotationNode;

/**
 * Asm annotation property.
 * @since 0.6
 */
final class AsmAnnotationProperty {

    /**
     * Property name.
     */
    private final String name;

    /**
     * Property value.
     */
    private final Object value;

    /**
     * Constructor.
     * @param value Property value.
     */
    AsmAnnotationProperty(final Object value) {
        this(null, value);
    }

    /**
     * Constructor.
     * @param name Property name.
     * @param value Property value.
     */
    AsmAnnotationProperty(final String name, final Object value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Convert asm annotation property to domain annotation property.
     * @return Domain annotation.
     */
    BytecodeAnnotationValue bytecode() {
        return AsmAnnotationProperty.property(this.name, this.value);
    }

    /**
     * Convert asm annotation property to domain annotation property.
     * @param name Property name.
     * @param value Property value.
     * @return Domain annotation.
     */
    private static BytecodeAnnotationValue property(final String name, final Object value) {
        final BytecodeAnnotationValue result;
        if (value instanceof String[]) {
            final String[] params = (String[]) value;
            result = new BytecodeEnumAnnotationValue(name, params[0], params[1]);
        } else if (value instanceof AnnotationNode) {
            final AnnotationNode cast = AnnotationNode.class.cast(value);
            result = new BytecodeAnnotationAnnotationValue(
                name,
                cast.desc,
                Optional.ofNullable(cast.values)
                    .map(Collection::stream)
                    .orElseGet(Stream::empty)
                    .map(val -> AsmAnnotationProperty.property("", val))
                    .collect(Collectors.toList())
            );
        } else if (value instanceof List) {
            result = new BytecodeArrayAnnotationValue(
                name,
                ((Collection<?>) value).stream()
                    .map(val -> AsmAnnotationProperty.property("", val))
                    .collect(Collectors.toList())
            );
        } else {
            result = new BytecodePlainAnnotationValue(name, value);
        }
        return result;
    }

}
