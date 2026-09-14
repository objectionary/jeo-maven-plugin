/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.Opcodes;

/**
 * Mirror for {@link org.eolang.jeo.representation.directives.DirectivesFrameValues}.
 * This class parses the frame values and their aliases.
 *
 * @since 0.14.0
 */
final class XmlFrameValues {

    /**
     * Frame value aliases mapped to their ASM opcode.
     */
    private static final Map<String, Integer> ALIASES = XmlFrameValues.aliases();

    /**
     * Xmir node representing frame values.
     */
    private final XmlNode root;

    /**
     * Constructor.
     *
     * @param root Xmir node representing frame values
     */
    XmlFrameValues(final XmlNode root) {
        this.root = root;
    }

    /**
     * Parse values from the Xmir node.
     *
     * @return Parsed values as an array of objects
     */
    Object[] values() {
        return Arrays.stream(new XmlValues(this.root).values())
            .map(XmlFrameValues::parse)
            .toArray();
    }

    private static Object parse(final Object value) {
        final Object result;
        if (value instanceof String && XmlFrameValues.ALIASES.containsKey(value)) {
            result = XmlFrameValues.ALIASES.get(value);
        } else {
            result = value;
        }
        return result;
    }

    private static Map<String, Integer> aliases() {
        final Map<String, Integer> map = new HashMap<>();
        map.put("top", Opcodes.TOP);
        map.put("short", Opcodes.INTEGER);
        map.put("boolean", Opcodes.INTEGER);
        map.put("char", Opcodes.INTEGER);
        map.put("byte", Opcodes.INTEGER);
        map.put("integer", Opcodes.INTEGER);
        map.put("float", Opcodes.FLOAT);
        map.put("double", Opcodes.DOUBLE);
        map.put("long", Opcodes.LONG);
        map.put("null", Opcodes.NULL);
        map.put("uninit_this", Opcodes.UNINITIALIZED_THIS);
        return Collections.unmodifiableMap(map);
    }
}
