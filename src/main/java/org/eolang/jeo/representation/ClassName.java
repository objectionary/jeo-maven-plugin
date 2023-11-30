/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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
     * @return Package name in the following format: "org.eolang.jeo.representation.directives".
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

