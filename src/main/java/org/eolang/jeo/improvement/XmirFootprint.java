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
package org.eolang.jeo.improvement;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.stream.Collectors;
import org.eolang.jeo.Improvement;
import org.eolang.jeo.Representation;
import org.eolang.jeo.representation.XmirRepresentation;

/**
 * Footprint of the Xmir.
 *
 * @since 0.1.0
 */
public final class XmirFootprint implements Improvement {

    /**
     * Where to save the Xmir.
     */
    private final Path target;

    /**
     * Constructor.
     * @param home Where to save the Xmir.
     */
    public XmirFootprint(final Path home) {
        this.target = home;
    }

    @Override
    public Collection<Representation> apply(
        final Collection<? extends Representation> representations
    ) {
        return representations.stream()
            .map(Representation::toEO)
            .map(XmirRepresentation::new)
            .peek(this::tryToSave)
            .collect(Collectors.toList());
    }

    /**
     * Try to save XML to the target folder.
     * @param representation XML to save.
     */
    private void tryToSave(final Representation representation) {
        final String name = representation.name();
        final Path path = this.target.resolve("jeo")
            .resolve("xmir")
            .resolve(String.format("%s.xmir", name.replace('.', File.separatorChar)));
        try {
            Files.createDirectories(path.getParent());
            Files.write(
                path,
                representation.toEO().toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE_NEW
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't save XML to %s", path),
                exception
            );
        }
    }
}
