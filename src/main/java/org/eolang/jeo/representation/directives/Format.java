/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Output format of the XMIR representation.
 * @since 0.14.0
 */
public final class Format {

    /**
     * Should method modifiers be included in the output.
     * Expected to be a boolean value.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    public static final String COMMENTS = "comments";

    /**
     * Should method modifiers be included in the output.
     * Expected to be a boolean value.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    public static final String MODIFIERS = "modifiers";

    /**
     * Bytecode listing.
     * Expected to be a string value.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    public static final String LISTING = "listing";

    /**
     * Bytecode listing.
     * Expected to be a string value.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    public static final String WITH_LISTING = "with_listing";

    /**
     * Pretty print the output.
     * Expected to be a boolean value.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    public static final String PRETTY = "pretty";

    /**
     * Disassemble mode.
     * Expected to be a string value.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    public static final String MODE = "mode";

    /**
     * All properties of the format.
     */
    private final Map<String, Object> properties;

    /**
     * Prepare pairs of properties.
     * @param pairs Pairs of properties, each pair consists of a name and a value.
     */
    public Format(final Object... pairs) {
        this(Format.pairs(Format.defaults(), pairs));
    }

    /**
     * Prepare pairs of properties.
     * @param format Existing format to extend.
     * @param pairs Pairs of properties, each pair consists of a name and a value.
     */
    public Format(final Format format, final Object... pairs) {
        this(Format.pairs(new HashMap<>(format.properties), pairs));
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
     * Should comments be included in the output.
     * @return True if comments are included, false otherwise.
     */
    public boolean comments() {
        return this.bool(Format.COMMENTS);
    }

    /**
     * Should the output be pretty printed.
     * @return True if pretty printing is enabled, false otherwise.
     */
    public boolean pretty() {
        return this.bool(Format.PRETTY);
    }

    /**
     * Get the listing property.
     * @return Listing value of the property.
     */
    public String listing() {
        return this.string(Format.LISTING);
    }

    /**
     * Should listing be included in the output.
     * @return True if listing is included, false otherwise.
     */
    public boolean withListing() {
        return this.bool(Format.WITH_LISTING);
    }

    /**
     * Get the mode property.
     * @return Mode value of the property.
     */
    public String mode() {
        return this.string(Format.MODE);
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
     * String property.
     * @param name Name of the property.
     * @return String value of the property.
     */
    private String string(final String name) {
        final String result;
        if (this.properties.containsKey(name)) {
            final Object value = this.properties.get(name);
            if (value instanceof String) {
                result = (String) value;
            } else {
                throw new IllegalArgumentException(
                    String.format("Property '%s' is not a string", name)
                );
            }
        } else {
            result = "";
        }
        return result;
    }

    /**
     * Parse pairs of properties into a map.
     * @param map Initial map to populate.
     * @param properties Pairs of properties, each pair consists of a name and a value.
     * @return Map of properties.
     */
    private static Map<String, Object> pairs(
        final Map<String, Object> map, final Object... properties
    ) {
        if (properties.length % 2 != 0) {
            throw new IllegalArgumentException(
                "Properties must be in pairs: name and value"
            );
        }
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

    /**
     * Default format.
     * @return Default format with comments enabled and modifiers disabled.
     */
    private static Map<String, Object> defaults() {
        final Map<String, Object> props = new HashMap<>(2);
        props.put(Format.COMMENTS, true);
        props.put(Format.MODIFIERS, false);
        props.put(Format.LISTING, "");
        props.put(Format.WITH_LISTING, false);
        props.put(Format.PRETTY, true);
        props.put(Format.MODE, "short");
        return props;
    }
}
