/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link JcabiXmlDoc}.
 * @since 0.8
 */
final class JcabiXmlDocTest {

    /**
     * Example XML.
     */
    private static final String XML = "<program><o>1</o></program>";

    @Test
    void createsFromFile(@TempDir final Path dir) throws IOException {
        final Path path = dir.resolve("test.xml");
        Files.write(path, JcabiXmlDocTest.XML.getBytes(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(
            "Can't read XML from file",
            new JcabiXmlDoc(path).root().xpath("/program/o/text()").get(0),
            org.hamcrest.Matchers.equalTo("1")
        );
    }

    @Test
    void findsFirstChildInFileWithComment(@TempDir final Path dir) throws IOException {
        final Path path = dir.resolve("test.xml");
        Files.write(
            path,
            String.format("<!-- Some comment -->%s", JcabiXmlDocTest.XML)
                .getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "Can't read XML from file",
            new JcabiXmlDoc(path).root().firstChild(),
            org.hamcrest.Matchers.equalTo(new JcabiXmlNode("<o>1</o>"))
        );
    }
}
