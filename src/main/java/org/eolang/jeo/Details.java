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
package org.eolang.jeo;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Details of representation.
 * Additional info about representation.
 * @since 0.1
 * @todo #448:30min Continue refactoring of the Details#source() method.
 *  Here we have a method that returns an Optional Path and it is not clear why
 *  it is returning an Optional instead of just Path.
 *  We either should refactor this method to return a Path or justify why it is so.
 */
public class Details {

    /**
     * Name of the class or an object.
     */
    private static final String NAME_KEY = "name";

    /**
     * Original source of the representation.
     * Might be a file name or a URL:
     * - Application.java
     * - Application.xmir
     */
    private static final String SOURCE_KEY = "source";

    /**
     * Storage with all the details.
     */
    private final Map<String, Object> storage;

    /**
     * Constructor.
     * @param name Name of the class or an object.
     * @param source Original source of the representation.
     */
    public Details(final String name, final Object source) {
        this(Details.NAME_KEY, name, Details.SOURCE_KEY, source);
    }

    /**
     * Constructor.
     * @param inits Initializations.
     */
    private Details(final Object... inits) {
        this(Details.initial(inits));
    }

    /**
     * Constructor.
     * @param storage Storage with all the details.
     */
    private Details(final Map<String, Object> storage) {
        this.storage = storage;
    }

    /**
     * Name of the class or an object.
     * @return Name.
     */
    public String name() {
        return (String) this.storage.get(Details.NAME_KEY);
    }

    /**
     * Original source of the representation.
     * It could be a file name or a URL.
     * @return Optional Source.
     */
    public Optional<Path> source() {
        return Optional.ofNullable(this.storage.get(Details.SOURCE_KEY))
            .filter(Path.class::isInstance)
            .map(Path.class::cast);
    }

    public Optional<Path> destination() {
        return Optional.ofNullable(this.storage.get("destination"))
            .filter(Path.class::isInstance)
            .map(Path.class::cast);
    }

    public void destination(final Path destination) {
        this.storage.put("destination", destination);
    }

    /**
     * Initializations.
     * @param pairs Pairs of key-value.
     * @return Map with all the details.
     */
    private static Map<String, Object> initial(final Object... pairs) {
        final int length = pairs.length;
        if (length % 2 == 1) {
            throw new IllegalArgumentException("Must have an even number of arguments");
        }
        final Map<String, Object> map = new HashMap<>(pairs.length / 2);
        for (int index = 0; index < length; index += 2) {
            map.put((String) pairs[index], pairs[index + 1]);
        }
        return map;
    }
}
