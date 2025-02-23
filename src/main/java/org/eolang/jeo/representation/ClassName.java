/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class name.
 * Understands class name and package. Could extract them from full class name.
 * @since 0.1
 */
public final class ClassName {

    /**
     * Internal delimiter.
     * This delimiter is used to split full class name to package and class name.
     * The field {@code #name} uses this delimiter internally.
     */
    private static final String DELIMITER = "/";

    /**
     * Full class name.
     * This field contains full class name including package.
     * Example: {@code org/eolang/jeo/representation/directives/ClassName}.
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
     * @param pckg Package
     * @param name Class name
     */
    public ClassName(final String pckg, final String name) {
        this(Stream.of(pckg, name)
            .filter(s -> !s.isEmpty())
            .map(s -> s.replace(".", ClassName.DELIMITER))
            .collect(Collectors.joining(ClassName.DELIMITER)));
    }

    /**
     * Constructor.
     * @param name Full class name.
     */
    public ClassName(final String name) {
        this.fqn = name;
    }

    /**
     * Full class name.
     * @return Full class name as is.
     */
    public String full() {
        return this.fqn;
    }

    /**
     * Package.
     * @return Package name in the following format: "jeo.representation.directives".
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
     * @return Class name in the following format: {@code ClassName}.
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
