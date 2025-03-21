/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.exceptions;

import java.lang.IllegalStateException;

/**
 * Resource that used in {@link Application}.
 * We need it to test try-with-resources statement and suppressed exceptions.
 */
public class ResourceWithException implements AutoCloseable {

    @Override
    public void close() {
        throw new IllegalStateException("Exception during closing resource");
    }
}
