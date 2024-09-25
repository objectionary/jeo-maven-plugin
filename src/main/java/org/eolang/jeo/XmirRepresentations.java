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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.eolang.jeo.representation.XmirRepresentation;

/**
 * EO objects.
 * Reads all EO objects from the folder.
 *
 * @since 0.1.0
 */
final class XmirRepresentations implements Representations {

    /**
     * Where to read objects from.
     * Usually it's a folder with the name "generated-sources".
     * See <a href="https://maven.apache.org/guides/mini/guide-generating-sources.html">generated-sources</a>.
     */
    private final Path objectspath;

    /**
     * Verify bytecode.
     * @since 0.2
     */
    private final boolean verify;

    /**
     * Constructor.
     * @param objectspath Where to read objects from.
     */
    XmirRepresentations(final Path objectspath) {
        this(objectspath, true);
    }

    /**
     * Constructor.
     * @param objectspath Where to read objects from.
     * @param verify Verify bytecode.
     */
    XmirRepresentations(final Path objectspath, final boolean verify) {
        this.objectspath = objectspath;
        this.verify = verify;
    }

    @Override
    public Stream<? extends Representation> all() {
        final Path path = this.objectspath;
        try {
            return Files.walk(path)
                .filter(Files::isRegularFile)
                .map(p -> new XmirRepresentation(p, this.verify));
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't read folder '%s'", path),
                exception
            );
        }
    }

}
