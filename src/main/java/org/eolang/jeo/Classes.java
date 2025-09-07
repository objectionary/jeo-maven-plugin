/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Representation of all class files.
 * @since 0.14.0
 */
interface Classes {

    /**
     * All the class files.
     * @return Stream of paths to class files
     */
    Stream<Path> all();
}
