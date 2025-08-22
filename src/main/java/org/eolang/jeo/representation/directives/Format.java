/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Output format of the XMIR representation.
 * @since 0.14.0
 * @todo #1263:90min Use Format class instead of DisassembleParams.
 *  DisassembleParams is rather rigid and cannot be extended with new properties.
 *  Moreover, some of the properties in DisassembleParams are related to disassembling
 *  in general and not to formatting specifically. We should move all formatting-related
 *  properties to Format class and use it in all places where formatting is needed.
 *  {@link org.eolang.jeo.representation.asm.DisassembleParams}
 */
public final class Format {

    /**
     * Should method modifiers be included in the output.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    public static final String MODIFIERS = "modifiers";

    /**
     * All properties of the format.
     */
    private final Map<String, Object> properties;

    /**
     * Prepare pairs of properties.
     * @param pairs Pairs of properties, each pair consists of a name and a value.
     */
    public Format(final Object... pairs) {
        this(Format.pairs(pairs));
    }

    /**
     * Constructor.
     * @param properties Properties of the format.
     */
    private Format(final Map<String, Object> properties) {
        this.properties = new ConcurrentHashMap<>(properties);
    }

    /**
     * Should method modifiers be included in the output.
     * @return True if modifiers are included, false otherwise.
     */
    public boolean modifiers() {
        return this.bool(Format.MODIFIERS);
    }

    /**
     * Get the boolean property.
     * @param name Name of the property.
     * @return Boolean value of the property.
     */
    private boolean bool(final String name) {
        final boolean result;
        if (this.properties.containsKey(name)) {
            final Object value = this.properties.get(name);
            if (value instanceof Boolean) {
                result = (Boolean) value;
            } else {
                throw new IllegalArgumentException(
                    String.format("Property '%s' is not a boolean", name)
                );
            }
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Parse pairs of properties into a map.
     * @param properties Pairs of properties, each pair consists of a name and a value.
     * @return Map of properties.
     */
    private static Map<String, Object> pairs(final Object... properties) {
        if (properties.length % 2 != 0) {
            throw new IllegalArgumentException(
                "Properties must be in pairs: name and value"
            );
        }
        final Map<String, Object> map = new java.util.HashMap<>();
        for (int index = 0; index < properties.length; index += 2) {
            if (!(properties[index] instanceof String)) {
                throw new IllegalArgumentException(
                    String.format("Property name '%s' is not a string", properties[index])
                );
            }
            map.put((String) properties[index], properties[index + 1]);
        }
        return Collections.unmodifiableMap(map);
    }
}
