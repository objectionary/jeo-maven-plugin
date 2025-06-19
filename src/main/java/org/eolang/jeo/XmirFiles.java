/*
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
 * XMIR files reader.
 *
 * <p>This class reads all XMIR (EO XML representation) files from a specified folder.
 * It provides functionality to discover and validate XMIR files for processing.</p>
 * @since 0.1.0
 */
final class XmirFiles {

    /**
     * Where to read objects from.
     * <p>Usually it's a folder with the name "generated-sources".
     * See <a href="https://maven.apache.org/guides/mini/guide-generating-sources.html">Maven Guide: Generating Sources</a>.</p>
     */
    private final Path root;

    /**
     * Constructor.
     * @param xmirs Root directory containing XMIR files
     */
    XmirFiles(final Path xmirs) {
        this.root = xmirs;
    }

    /**
     * All representations.
     * @return Stream of paths to all XMIR files in the directory tree
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
     * Verify all the XMIR files.
     */
    public void verify() {
        this.all().map(JcabiXmlDoc::new).forEach(JcabiXmlDoc::validate);
    }
}
