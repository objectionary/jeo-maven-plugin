/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeAttribute;

/**
 * NestMembers attribute representation.
 * @since 0.14.0
 */
final class XmlNestMembers {

    /**
     * JEO XML node.
     */
    private final XmlJeoObject node;

    XmlNestMembers(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Parse bytecode attribute.
     * @return Bytecode attribute.
     */
    BytecodeAttribute attribute() {
        return new BytecodeAttribute.NestMembers(
            Arrays.stream(
                new XmlValues(
                    this.node.child(0).orElseThrow(
                        () -> new IllegalArgumentException(
                            String.format("NestMembers attribute is malformed: %s", this.node)
                        )
                    )
                ).values()
            ).map(XmlNestMembers::member).collect(Collectors.toList())
        );
    }

    /**
     * Parse member.
     * @param memb Member object.
     * @return Member internal name.
     */
    private static String member(final Object memb) {
        if (memb instanceof String) {
            return (String) memb;
        } else {
            throw new IllegalStateException(
                String.format("Unexpected member type: %s", memb.getClass())
            );
        }
    }
}
