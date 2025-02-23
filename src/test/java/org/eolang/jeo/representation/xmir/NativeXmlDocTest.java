/*
 * The MIT License (MIT)
 *
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
 * Test case for {@link NativeXmlDoc}.
 * @since 0.8
 */
final class NativeXmlDocTest {

    /**
     * Example XML.
     */
    private static final String XML = "<root><a>1</a><b>2</b></root>";

    @Test
    void createsFromFile(@TempDir final Path dir) throws IOException {
        final Path path = dir.resolve("test.xml");
        Files.write(path, NativeXmlDocTest.XML.getBytes(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(
            "Can't read XML from file",
            new NativeXmlDoc(path).root().xpath("/root/a/text()").get(0),
            org.hamcrest.Matchers.equalTo("1")
        );
    }
}
