/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.eolang.jeo.representation.xmir.JcabiXmlDoc;

/**
 * EO objects.
 * Reads all EO objects from the folder.
 *
 * @since 0.1.0
 */
final class XmirFiles {

    /**
     * Where to read objects from.
     * Usually it's a folder with the name "generated-sources".
     * See <a href="https://maven.apache.org/guides/mini/guide-generating-sources.html">generated-sources</a>.
     */
    private final Path root;

    /**
     * Constructor.
     * @param xmirs Where to read objects from.
     */
    XmirFiles(final Path xmirs) {
        this.root = xmirs;
    }

    /**
     * All representations.
     * @return All representations.
     */
    public Stream<Path> all() {
        final Path path = this.root;
        try {
            return Files.walk(path).filter(Files::isRegularFile);
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't read folder '%s'", path),
                exception
            );
        }
    }

    /**
     * Verify all the xmir files.
     */
    public void verify() {
        this.all().map(JcabiXmlDoc::new).forEach(JcabiXmlDoc::validate);
    }
}
