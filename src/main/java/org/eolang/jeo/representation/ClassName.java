/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Class name parser and builder.
 *
 * <p>This class understands Java class names and packages. It can extract package
 * and class name components from a fully qualified class name, and construct
 * full names from separate package and class name parts.</p>
 * @since 0.1.0
 */
@EqualsAndHashCode
@ToString
public final class ClassName {

    /**
     * Internal delimiter.
     * <p>This delimiter is used to split full class name to package and class name.
     * The field {@link #fqn} uses this delimiter internally.</p>
     */
    private static final String SLASH = "/";

    /**
     * Dot pattern.
     * Used to convert dotted notation to slashed notation.
     */
    private static final Pattern DOT_PATTERN = Pattern.compile(".", Pattern.LITERAL);

    /**
     * Slash pattern.
     * Used to convert slashed notation to dotted notation.
     */
    private static final Pattern SLASH_PATTERN = Pattern.compile(ClassName.SLASH, Pattern.LITERAL);

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
     * @param name The full class name with slash delimiters
     */
    public ClassName(final String name) {
        this.fqn = ClassName.slashed(name);
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
        final int index = this.fqn.lastIndexOf(ClassName.SLASH);
        if (index == -1) {
            result = "";
        } else {
            result = ClassName.dotted(this.fqn.substring(0, index));
        }
        return result;
    }

    /**
     * Class name.
     * @return Simple class name without package (e.g., "ClassName")
     */
    public String name() {
        final String result;
        if (this.fqn.contains(ClassName.SLASH)) {
            final String[] split = this.fqn.split(ClassName.SLASH);
            result = split[split.length - 1];
        } else {
            result = this.fqn;
        }
        return result;
    }

    /**
     * Convert string with dots to slashed notation.
     * @param origin String with dots (e.g., "org.eolang.jeo.representation")
     * @return String with slashes (e.g., "org/eolang/jeo/representation")
     */
    private static String slashed(final String origin) {
        return ClassName.DOT_PATTERN.matcher(origin).replaceAll(ClassName.SLASH);
    }

    /**
     * Convert string with slashes to dotted notation.
     * @param origin String with slashes (e.g., "org/eolang/jeo/representation")
     * @return String with dots (e.g., "org.eolang.jeo.representation")
     */
    private static String dotted(final String origin) {
        return ClassName.SLASH_PATTERN.matcher(origin).replaceAll(".");
    }
}
