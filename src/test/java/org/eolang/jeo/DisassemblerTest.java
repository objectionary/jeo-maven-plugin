/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test cases for {@link Disassembler}.
 *
 * @since 0.15
 */
final class DisassemblerTest {

    @Test
    void reportsTheSizeOfTheFreshFile(@TempDir final Path temp) throws Exception {
        final Path classes = temp.resolve("classes");
        Files.createDirectories(classes);
        Files.write(
            classes.resolve("MethodByte.class"),
            new BytesOf(new ResourceOf("MethodByte.class")).asBytes()
        );
        final StringWriter logs = new StringWriter();
        final Appender appender = new WriterAppender(new SimpleLayout(), logs);
        final Logger logger = Logger.getLogger(Logging.class);
        logger.addAppender(appender);
        try {
            new Disassembler(classes, temp.resolve("xmir")).disassemble();
        } finally {
            logger.removeAppender(appender);
        }
        MatcherAssert.assertThat(
            "A freshly disassembled file must not be reported as empty",
            logs.toString(),
            Matchers.allOf(
                Matchers.containsString("MethodByte.xmir"),
                Matchers.not(Matchers.containsString("(0b)"))
            )
        );
    }

    @Test
    void walksClassesOnlyOnce(@TempDir final Path temp) throws Exception {
        final Path classes = temp.resolve("classes");
        Files.createDirectories(classes);
        Files.write(
            classes.resolve("MethodByte.class"),
            new BytesOf(new ResourceOf("MethodByte.class")).asBytes()
        );
        final List<String> logs = new CopyOnWriteArrayList<>();
        new Disassembler(
            new FilteredClasses(
                new BytecodeClasses(classes),
                new GlobFilter(Collections.singleton("**"), Collections.emptySet()),
                logs::add
            ),
            temp.resolve("xmir"),
            new Format(),
            false
        ).disassemble();
        MatcherAssert.assertThat(
            "The list of classes must be found once per run, not once for counting and once for work",
            logs,
            Matchers.hasSize(1)
        );
    }

    @Test
    void removesXmirOfDeletedClass(@TempDir final Path temp) throws Exception {
        final Path classes = temp.resolve("classes");
        Files.createDirectories(classes.resolve("extra"));
        final byte[] bytes = new BytesOf(new ResourceOf("MethodByte.class")).asBytes();
        Files.write(classes.resolve("MethodByte.class"), bytes);
        Files.write(classes.resolve("extra").resolve("MethodByte.class"), bytes);
        final Path xmir = temp.resolve("xmir");
        new Disassembler(classes, xmir).disassemble();
        Files.delete(classes.resolve("extra").resolve("MethodByte.class"));
        new Disassembler(classes, xmir).disassemble();
        MatcherAssert.assertThat(
            "The XMIR of a class that is gone must be removed, or assemble brings the class back",
            Files.exists(xmir.resolve("extra").resolve("MethodByte.xmir")),
            Matchers.is(false)
        );
    }

    @Test
    void keepsXmirOfFilteredOutClass(@TempDir final Path temp) throws Exception {
        final Path classes = temp.resolve("classes");
        Files.createDirectories(classes.resolve("com").resolve("a"));
        Files.createDirectories(classes.resolve("com").resolve("b"));
        final byte[] bytes = new BytesOf(new ResourceOf("MethodByte.class")).asBytes();
        Files.write(classes.resolve("com").resolve("a").resolve("MethodByte.class"), bytes);
        Files.write(classes.resolve("com").resolve("b").resolve("MethodByte.class"), bytes);
        final Path xmir = temp.resolve("xmir");
        new Disassembler(classes, xmir).disassemble();
        new Disassembler(
            new FilteredClasses(
                new BytecodeClasses(classes),
                new GlobFilter(Collections.singleton("com/a/**"), Collections.emptySet())
            ),
            xmir,
            new Format(),
            false
        ).disassemble();
        MatcherAssert.assertThat(
            "The XMIR of a class skipped by includes must stay on disk",
            Files.exists(xmir.resolve("com").resolve("b").resolve("MethodByte.xmir")),
            Matchers.is(true)
        );
    }

    @Test
    void keepsCacheOfFilteredOutClass(@TempDir final Path temp) throws Exception {
        final Path classes = temp.resolve("classes");
        Files.createDirectories(classes.resolve("com").resolve("a"));
        Files.createDirectories(classes.resolve("com").resolve("b"));
        final byte[] bytes = new BytesOf(new ResourceOf("MethodByte.class")).asBytes();
        Files.write(classes.resolve("com").resolve("a").resolve("MethodByte.class"), bytes);
        Files.write(classes.resolve("com").resolve("b").resolve("MethodByte.class"), bytes);
        final Path xmir = temp.resolve("xmir");
        new Disassembler(classes, xmir).disassemble();
        new Disassembler(
            new FilteredClasses(
                new BytecodeClasses(classes),
                new GlobFilter(Collections.singleton("com/a/**"), Collections.emptySet())
            ),
            xmir,
            new Format(),
            false
        ).disassemble();
        MatcherAssert.assertThat(
            "The cache of a class skipped by includes must stay on disk",
            Files.exists(xmir.resolve("com").resolve("b").resolve("MethodByte.xmir.jeo-cache")),
            Matchers.is(true)
        );
    }

    @Test
    void keepsXmirOfExcludedClass(@TempDir final Path temp) throws Exception {
        final Path classes = temp.resolve("classes");
        Files.createDirectories(classes.resolve("com").resolve("a"));
        Files.createDirectories(classes.resolve("com").resolve("b"));
        final byte[] bytes = new BytesOf(new ResourceOf("MethodByte.class")).asBytes();
        Files.write(classes.resolve("com").resolve("a").resolve("MethodByte.class"), bytes);
        Files.write(classes.resolve("com").resolve("b").resolve("MethodByte.class"), bytes);
        final Path xmir = temp.resolve("xmir");
        new Disassembler(classes, xmir).disassemble();
        new Disassembler(
            new FilteredClasses(
                new BytecodeClasses(classes),
                new GlobFilter(Collections.emptySet(), Collections.singleton("com/b/**"))
            ),
            xmir,
            new Format(),
            false
        ).disassemble();
        MatcherAssert.assertThat(
            "The XMIR of an excluded class whose .class still exists must stay on disk",
            Files.exists(xmir.resolve("com").resolve("b").resolve("MethodByte.xmir")),
            Matchers.is(true)
        );
    }

    @Test
    void removesXmirOfDeletedClassEvenWithExcludes(@TempDir final Path temp)
        throws Exception {
        final Path classes = temp.resolve("classes");
        Files.createDirectories(classes.resolve("com").resolve("a"));
        Files.createDirectories(classes.resolve("com").resolve("b"));
        final byte[] bytes = new BytesOf(new ResourceOf("MethodByte.class")).asBytes();
        Files.write(classes.resolve("com").resolve("a").resolve("MethodByte.class"), bytes);
        Files.write(classes.resolve("com").resolve("b").resolve("MethodByte.class"), bytes);
        final Path xmir = temp.resolve("xmir");
        new Disassembler(classes, xmir).disassemble();
        final Classes filtered = new FilteredClasses(
            new BytecodeClasses(classes),
            new GlobFilter(Collections.emptySet(), Collections.singleton("com/b/**"))
        );
        Files.delete(classes.resolve("com").resolve("b").resolve("MethodByte.class"));
        new Disassembler(filtered, xmir, new Format(), false).disassemble();
        MatcherAssert.assertThat(
            "The XMIR of a deleted class must be removed even when excludes match its path",
            Files.exists(xmir.resolve("com").resolve("b").resolve("MethodByte.xmir")),
            Matchers.is(false)
        );
    }

    @Test
    void disassemblesClassesFromSymlinkedDirectory(@TempDir final Path temp) throws Exception {
        final Path target = temp.resolve("target");
        Files.createDirectories(target);
        Files.write(
            target.resolve("MethodByte.class"),
            new BytesOf(new ResourceOf("MethodByte.class")).asBytes()
        );
        final Path linked = temp.resolve("linked");
        DisassemblerTest.symlink(linked, target);
        final Path xmir = temp.resolve("xmir");
        new Disassembler(linked, xmir).disassemble();
        MatcherAssert.assertThat(
            "Disassembler should produce XMIR when sourcesDir is a symbolic link",
            Files.exists(xmir.resolve("MethodByte.xmir")),
            Matchers.is(true)
        );
    }

    @Test
    void disassemblesClassesFromSymlinkedSubdirectory(@TempDir final Path temp) throws Exception {
        final Path target = temp.resolve("target");
        Files.createDirectories(target);
        Files.write(
            target.resolve("MethodByte.class"),
            new BytesOf(new ResourceOf("MethodByte.class")).asBytes()
        );
        final Path classes = temp.resolve("classes");
        Files.createDirectories(classes);
        DisassemblerTest.symlink(classes.resolve("sub"), target);
        final Path xmir = temp.resolve("xmir");
        new Disassembler(classes, xmir).disassemble();
        MatcherAssert.assertThat(
            "Disassembler should traverse symlinked subdirectories under sourcesDir",
            Files.exists(xmir.resolve("sub").resolve("MethodByte.xmir")),
            Matchers.is(true)
        );
    }

    @Test
    void preservesExternalFilesThroughNestedOutputSymlink(@TempDir final Path temp)
        throws Exception {
        final Path classes = temp.resolve("classes");
        Files.createDirectories(classes);
        final Path external = temp.resolve("external");
        Files.createDirectories(external);
        final Path keep = external.resolve("Keep.xmir");
        final Path cache = external.resolve("Keep.xmir.jeo-cache");
        Files.write(keep, "<object/>".getBytes(StandardCharsets.UTF_8));
        Files.write(cache, "1".getBytes(StandardCharsets.UTF_8));
        final Path output = temp.resolve("output");
        Files.createDirectories(output);
        DisassemblerTest.symlink(output.resolve("shared"), external);
        new Disassembler(classes, output).disassemble();
        MatcherAssert.assertThat(
            "Cleanup must not delete external XMIR files through nested output symlinks",
            Files.exists(keep),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "Cleanup must not delete external cache files through nested output symlinks",
            Files.exists(cache),
            Matchers.is(true)
        );
    }

    private static void symlink(final Path link, final Path target) throws IOException {
        try {
            Files.createSymbolicLink(link, target);
        } catch (final FileSystemException exception) {
            Assumptions.assumeTrue(false, exception.getMessage());
        }
    }
}
