/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class name parser and builder.
 * <p>This class understands Java class names and packages. It can extract package
 * and class name components from a fully qualified class name, and construct
 * full names from separate package and class name parts.</p>
 * @since 0.1.0
 */
public final class ClassName {

    /**
     * Internal delimiter.
     * <p>This delimiter is used to split full class name to package and class name.
     * The field {@link #fqn} uses this delimiter internally.</p>
     */
    private static final String DELIMITER = "/";

    /**
     * Full class name.
     * <p>This field contains full class name including package.
     * Example: {@code org/eolang/jeo/representation/directives/ClassName}.</p>
     */
    private final String fqn;

    /**
     * Constructor.
     */
    public ClassName() {
        this("HelloWorld");
    }

    /**
     * Constructor.
     * @param pckg Package name (can be empty)
     * @param name Simple class name
     */
    public ClassName(final String pckg, final String name) {
        this(Stream.of(pckg, name)
            .filter(s -> !s.isEmpty())
            .map(s -> s.replace(".", ClassName.DELIMITER))
            .collect(Collectors.joining(ClassName.DELIMITER)));
    }

    /**
     * Constructor.
     * @param name Full class name with slash delimiters
     */
    public ClassName(final String name) {
        this.fqn = name;
    }

    /**
     * Full class name.
     * @return Full class name with slash delimiters
     */
    public String full() {
        return this.fqn;
    }

    /**
     * Package.
     * @return Package name in dot notation (e.g., "jeo.representation.directives")
     */
    public String pckg() {
        final String result;
        final int index = this.fqn.lastIndexOf(ClassName.DELIMITER);
        if (index == -1) {
            result = "";
        } else {
            result = this.fqn.substring(0, index).replace(ClassName.DELIMITER, ".");
        }
        return result;
    }

    /**
     * Class name.
     * @return Simple class name without package (e.g., "ClassName")
     */
    public String name() {
        final String result;
        if (this.fqn.contains(ClassName.DELIMITER)) {
            final String[] split = this.fqn.split(ClassName.DELIMITER);
            result = split[split.length - 1];
        } else {
            result = this.fqn;
        }
        return result;
    }
}
