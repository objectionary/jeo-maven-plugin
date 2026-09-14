/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class name parser and builder.
 *
 * <p>This class understands Java class names and packages. It can extract package and class name
 * components from a fully qualified class name, and construct full names from separate package
 * and class name parts.</p>
 *
 * @since 0.1.0
 */
public final class ClassName {

    /**
     * Internal delimiter.
     *
     * <p>This delimiter is used to split full class name to package and class name. The field
     * {@link #fqn} uses this delimiter internally.</p>
     */
    private static final String SLASH = "/";

    /**
     * Full class name.
     *
     * <p>This field contains full class name including package. Example: {@code
     * org/eolang/jeo/representation/directives/ClassName}.</p>
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
     *
     * @param pckg The package name (can be empty)
     * @param name The simple class name
     */
    public ClassName(final String pckg, final String name) {
        this(Stream.of(pckg, name)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.joining(ClassName.SLASH)));
    }

    /**
     * Constructor.
     *
     * @param name The full class name with slash delimiters
     */
    public ClassName(final String name) {
        this.fqn = name;
    }

    /**
     * Full class name.
     *
     * @return Full class name with slash delimiters
     */
    public String full() {
        return ClassName.slashed(this.fqn);
    }

    /**
     * Package.
     *
     * @return Package name in dot notation (e.g., "jeo.representation.directives")
     */
    public String pckg() {
        final String result;
        final String slashed = ClassName.slashed(this.fqn);
        final int index = slashed.lastIndexOf(ClassName.SLASH);
        if (index == -1) {
            result = "";
        } else {
            result = ClassName.dotted(slashed.substring(0, index));
        }
        return result;
    }

    /**
     * Class name.
     *
     * @return Simple class name without package (e.g., "ClassName")
     */
    public String name() {
        final String result;
        final String slashed = ClassName.slashed(this.fqn);
        final int index = slashed.lastIndexOf(ClassName.SLASH);
        if (index == -1) {
            result = slashed;
        } else {
            result = slashed.substring(index + 1);
        }
        return result;
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof ClassName) {
            result = this.full().equals(((ClassName) other).full());
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return this.full().hashCode();
    }

    @Override
    public String toString() {
        return this.full();
    }

    private static String slashed(final String origin) {
        return origin.replace('.', '/');
    }

    private static String dotted(final String origin) {
        return origin.replace('/', '.');
    }
}
