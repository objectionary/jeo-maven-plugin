/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enumeration of all supported data types in bytecode representation.
 *
 * <p>This enumeration defines the mapping between Java data types and their
 * corresponding EO base types used in XMIR representation.</p>
 * @since 0.3.0
 */
enum DataType {

    /**
     * Boolean.
     */
    BOOL("bool", Boolean.class),

    /**
     * Character.
     */
    CHAR("char", Character.class),

    /**
     * Byte.
     */
    BYTE("byte", Byte.class),

    /**
     * Short.
     */
    SHORT("short", Short.class),

    /**
     * Integer.
     */
    INT("number", Integer.class),

    /**
     * Long.
     */
    LONG("long", Long.class),

    /**
     * Float.
     */
    FLOAT("float", Float.class),

    /**
     * Double.
     */
    DOUBLE("double", Double.class),

    /**
     * String.
     */
    STRING("string", String.class),

    /**
     * Bytes.
     */
    BYTES("bytes", byte[].class),

    /**
     * Null.
     */
    NULL("nullable", Void.class);

    /**
     * Base type.
     */
    private final String base;

    /**
     * Class.
     */
    private final Class<?> clazz;

    /**
     * Constructor.
     * @param base The EO base type name
     * @param clazz The corresponding Java class
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    DataType(final String base, final Class<?> clazz) {
        this.base = base;
        this.clazz = clazz;
    }

    /**
     * Find a data type by its EO base type name.
     * @param base The EO base type name
     * @return The corresponding DataType
     */
    static DataType findByBase(final String base) {
        return Arrays.stream(DataType.values())
            .filter(type -> type.base.equals(base))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("Unknown data type '%s'", base)
                )
            );
    }

    /**
     * Find a data type by examining a data object.
     * @param data The data object to analyze
     * @return The corresponding DataType
     */
    static DataType findByData(final Object data) {
        final DataType result;
        if (data == null) {
            result = DataType.NULL;
        } else {
            result = Arrays.stream(DataType.values()).filter(type -> type.clazz.isInstance(data))
                .findFirst()
                .orElseThrow(
                    () -> new IllegalArgumentException(
                        String.format(
                            "Unknown data type of %s, class is %s",
                            data,
                            Optional.of(data)
                                .map(Object::getClass)
                                .map(Class::getName)
                                .orElse("Nullable")
                        )
                    )
                );
        }
        return result;
    }

    /**
     * Get the EO base type name.
     * @return The EO base type name
     */
    String caption() {
        return this.base;
    }

}
