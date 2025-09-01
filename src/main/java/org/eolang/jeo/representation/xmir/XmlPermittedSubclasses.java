/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeAttribute;

/**
 * Xmir representation of PermittedSubclasses attribute.
 * @since 0.14.0
 */
final class XmlPermittedSubclasses {

    /**
     * JEO XML node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param node JEO XML node.
     */
    XmlPermittedSubclasses(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Bytecode attribute representation.
     * @return Bytecode attribute.
     */
    public BytecodeAttribute attribute() {
        return new BytecodeAttribute.PermittedSubclasses(
            Arrays.stream(
                new XmlValues(
                    this.node.child(0).orElseThrow(
                        () -> new IllegalArgumentException(
                            String.format(
                                "PermittedSubclasses attribute is malformed: %s", this.node
                            )
                        )
                    )
                ).values()
            ).map(XmlPermittedSubclasses::subclass).collect(Collectors.toList())
        );
    }

    /**
     * Parse subclass.
     * @param obj Raw subclass name.
     * @return Subclass internal name as string.
     */
    private static String subclass(final Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        } else {
            throw new IllegalStateException(
                String.format("Unexpected subclass type: %s", obj.getClass())
            );
        }
    }
}
