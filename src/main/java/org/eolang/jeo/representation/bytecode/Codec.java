/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

/**
 * Classes that can transform objects to byte arrays and vice versa.
 * @since 0.8
 */
public interface Codec {

    /**
     * Encodes an object to a byte array.
     * @param object Object.
     * @param type Data type.
     * @return Byte array.
     */
    byte[] encode(Object object, DataType type);

    /**
     * Decodes a byte array to an object.
     * @param bytes Byte array.
     * @param type Data type.
     * @return Object.
     */
    Object decode(byte[] bytes, DataType type);
}
