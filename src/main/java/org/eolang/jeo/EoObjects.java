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

import com.jcabi.xml.XMLDocument;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.EoRepresentation;

/**
 * EO objects.
 * Reads all EO objects from the folder.
 *
 * @since 0.1.0
 */
final class EoObjects {

    /**
     * Where to read objects from.
     */
    private final Path objectspath;

    /**
     * Constructor.
     * @param objects Where to read objects from.
     */
    EoObjects(final Path objects) {
        this.objectspath = objects;
    }

    /**
     * Read all objects.
     * @return All objects.
     */
    Collection<EoRepresentation> objects() {
        final Path path = this.objectspath.resolve(new EoDefaultDirectory().toPath());
        try (Stream<Path> walk = Files.walk(path)) {
            return walk.filter(Files::isRegularFile)
                .map(EoObjects::xml)
                .map(EoRepresentation::new)
                .collect(Collectors.toList());
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't read folder '%s'", path),
                exception
            );
        }
    }

    /**
     * Convert path to XML.
     * @param path Path to XML file.
     * @return XML.
     */
    private static XMLDocument xml(final Path path) {
        try {
            return new XMLDocument(path.toFile());
        } catch (final FileNotFoundException exception) {
            throw new IllegalStateException(
                String.format("Can't find file '%s'", path),
                exception
            );
        }
    }
}
