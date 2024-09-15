/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.ToString;

/**
 * Details of representation.
 * Additional info about representation.
 * @since 0.1
 */
@ToString
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
    private final Map<String, String> storage;

    /**
     * Constructor.
     * @param name Name of the class or an object.
     * @param source Original source of the representation.
     */
    public Details(final String name, final String source) {
        this(Details.NAME_KEY, name, Details.SOURCE_KEY, source);
    }

    /**
     * Constructor.
     * @param inits Initializations.
     */
    private Details(final String... inits) {
        this(Details.initial(inits));
    }

    /**
     * Constructor.
     * @param storage Storage with all the details.
     */
    private Details(final Map<String, String> storage) {
        this.storage = storage;
    }

    /**
     * Name of the class or an object.
     * @return Name.
     */
    public String name() {
        return this.storage.get(Details.NAME_KEY);
    }

    /**
     * Original source of the representation.
     * It could be a file name or a URL.
     * @return Optional Source.
     */
    public Optional<Path> source() {
        final Optional<Path> result;
        if (this.storage.containsKey(Details.SOURCE_KEY)) {
            final Path path = Paths.get(this.storage.get(Details.SOURCE_KEY)).toAbsolutePath();
            if (Files.exists(path)) {
                result = Optional.of(path);
            } else {
                result = Optional.empty();
            }
        } else {
            result = Optional.empty();
        }
        return result;
    }

    /**
     * Initializations.
     * @param pairs Pairs of key-value.
     * @return Map with all the details.
     */
    private static Map<String, String> initial(final String... pairs) {
        final int length = pairs.length;
        if (length % 2 == 1) {
            throw new IllegalArgumentException("Must have an even number of arguments");
        }
        final Map<String, String> map = new HashMap<>(pairs.length / 2);
        for (int index = 0; index < length; index += 2) {
            map.put(pairs[index], pairs[index + 1]);
        }
        return map;
    }
}
