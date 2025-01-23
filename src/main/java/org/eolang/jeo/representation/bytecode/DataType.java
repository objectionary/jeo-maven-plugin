/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Arrays;
import java.util.Optional;
import org.objectweb.asm.Type;

/**
 * All supported data types.
 * @since 0.3
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
    INT("int", Integer.class),

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
     * Label.
     */
    LABEL("label", BytecodeLabel.class),

    /**
     * Type reference.
     */
    TYPE_REFERENCE("type", Type.class),

    /**
     * Class reference.
     */
    CLASS_REFERENCE("class", Class.class),

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
     * @param base Base type.
     * @param clazz Class.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    DataType(final String base, final Class<?> clazz) {
        this.base = base;
        this.clazz = clazz;
    }

    /**
     * Find a type by base.
     * @param base Base type.
     * @return Type.
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
     * Find a type by data.
     * @param data Data.
     * @return Data type.
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
                            Optional.ofNullable(data)
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
     * Type name.
     * @return Type name.
     */
    String caption() {
        return this.base;
    }

}
