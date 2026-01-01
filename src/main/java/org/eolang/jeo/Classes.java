/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
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
     * Total number of class files.
     * @return Total count of class files
     */
    long total();

    /**
     * Root directory containing the class files.
     * @return Path to the root directory
     */
    Path root();

    /**
     * All the class files.
     * @return Stream of paths to class files
     */
    Stream<Path> all();
}
