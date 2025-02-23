/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

/**
 * Unsupported data type.
 * @since 0.8
 */
final class UnsupportedDataType extends IllegalArgumentException {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1_586L;

    /**
     * Constructor.
     * @param type Data type.
     */
    UnsupportedDataType(final DataType type) {
        super(String.format("Unsupported data type: %s", type));
    }
}
