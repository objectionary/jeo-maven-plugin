/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test cases for {@link XmirFiles}.
 * This class verifies the functionality of XMIR file operations,
 * including retrieval, validation, and error handling.
 *
 * @since 0.1.0
 */
final class XmirFilesTest {

    @Test
    void retrievesObjectsSuccessfully(@TempDir final Path temp) throws IOException {
        final int expected = 2;
        final Path directory = temp.resolve(Paths.get("xmirs"));
        Files.createDirectories(directory);
        Files.write(
            directory.resolve("first.xmir"),
            new BytecodeProgram(new BytecodeClass("org.jeo.First")).xml().toString()
                .getBytes(StandardCharsets.UTF_8)
        );
        Files.write(
            directory.resolve("second.xmir"),
            new BytecodeProgram(new BytecodeClass("org.jeo.Second")).xml().toString()
                .getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            String.format("Objects were not retrieved, we expected '%d' objects", expected),
            new XmirFiles(temp).all().collect(Collectors.toList()),
            Matchers.hasSize(expected)
        );
    }

    @Test
    void retrievesEmptyObjectsIfFolderIsEmpty(@TempDir final Path temp) throws IOException {
        Files.createDirectories(temp.resolve("some-path"));
        MatcherAssert.assertThat(
            "Objects were not retrieved, we expected empty list",
            new XmirFiles(temp).all().collect(Collectors.toList()),
            Matchers.empty()
        );
    }

    @Test
    void throwsExceptionIfFolderDoesNotExist(@TempDir final Path temp) {
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> new XmirFiles(temp.resolve("missing")).all(),
            "Exception was not thrown when folder does not exist"
        );
    }

    @Test
    void usesDifferentSubfolder(@TempDir final Path temp) throws IOException {
        final Path generated = temp.resolve("generated-sources");
        final Path path = generated.resolve("opeo-xmir");
        Files.createDirectories(path);
        Files.write(
            path.resolve("opeo-class.xmir"),
            new BytecodeProgram(new BytecodeClass("org.jeo.OpeoClass")).xml().toString()
                .getBytes(StandardCharsets.UTF_8)
        );
        final Stream<Path> all = new XmirFiles(path).all();
        MatcherAssert.assertThat(
            String.format(
                "Objects were not retrieved, we expected exactly one object was read from %s",
                path
            ),
            all.collect(Collectors.toList()),
            Matchers.hasSize(1)
        );
    }

    @Test
    void verifiesXmirFilesGeneratedFromBytecode(@TempDir final Path temp) throws IOException {
        Files.write(
            temp.resolve("MethodByte.xmir"),
            new BytecodeRepresentation(new ResourceOf("MethodByte.class"))
                .toEO()
                .toString()
                .getBytes(StandardCharsets.UTF_8)
        );
        Assertions.assertDoesNotThrow(
            () -> new XmirFiles(temp).verify(),
            "We expected no exceptions when verifying the correct xmir files"
        );
    }

    @Test
    void failsOnInvalidXmirFile(@TempDir final Path temp) throws IOException {
        Files.write(
            temp.resolve("Invalid.xmir"),
            "<program></program>".getBytes(StandardCharsets.UTF_8)
        );
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new XmirFiles(temp).verify(),
            "We expected an exception when verifying the invalid xmir files"
        );
    }

    @Test
    void throwsExceptionIfXmirIsInvalid(@TempDir final Path temp) throws IOException {
        Files.write(
            temp.resolve("MethodByte.xmir"),
            new BytecodeProgram(
                new BytecodeClass("org/eolang/foo/Math")
            ).xml().toString().replace("<head>package</head>", "")
                .getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "We expect that XSD violation message will be easily understandable by developers",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new XmirFiles(temp).verify()
            ).getMessage(),
            Matchers.containsString(
                "1 error(s) in XML document: cvc-complex-type.2.4.a: Invalid content was found starting with element 'tail'. One of '{head}' is expected."
            )
        );
    }
}
