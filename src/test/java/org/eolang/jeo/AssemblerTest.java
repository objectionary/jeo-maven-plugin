/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test cases for {@link Assembler}.
 *
 * @since 0.15
 */
final class AssemblerTest {

    @Test
    void doesNotReportCachedFileAsAssembled(@TempDir final Path temp) throws Exception {
        final Path classes = temp.resolve("classes");
        Files.createDirectories(classes);
        Files.write(
            classes.resolve("MethodByte.class"),
            new BytesOf(new ResourceOf("MethodByte.class")).asBytes()
        );
        final Path xmir = temp.resolve("xmir");
        new Disassembler(classes, xmir).disassemble();
        final Path output = temp.resolve("output");
        new Assembler(xmir, output, false).assemble();
        final StringWriter logs = new StringWriter();
        final Appender appender = new WriterAppender(new SimpleLayout(), logs);
        final Logger logger = Logger.getLogger(Logging.class);
        logger.addAppender(appender);
        try {
            new Assembler(xmir, output, false).assemble();
        } finally {
            logger.removeAppender(appender);
        }
        MatcherAssert.assertThat(
            "A file taken from the cache must not be reported as assembled",
            logs.toString(),
            Matchers.not(Matchers.containsString("assembled in"))
        );
    }

    @Test
    void logsSummaryEvenWhenLoggingFails(@TempDir final Path temp) throws Exception {
        final Path classes = temp.resolve("classes");
        Files.createDirectories(classes);
        Files.write(
            classes.resolve("MethodByte.class"),
            new BytesOf(new ResourceOf("MethodByte.class")).asBytes()
        );
        final Path xmir = temp.resolve("xmir");
        new Disassembler(classes, xmir).disassemble();
        final StringWriter logs = new StringWriter();
        final Appender summary = new WriterAppender(new SimpleLayout(), logs);
        final Logger sumlog = Logger.getLogger(Summary.class);
        final Logger asmlog = Logger.getLogger(Assembler.class);
        final Level previous = asmlog.getLevel();
        final Appender failing = new AppenderSkeleton() {
            @Override
            protected void append(final LoggingEvent event) {
                throw new IllegalStateException("Simulated log failure");
            }

            @Override
            public void close() {
                // Nothing to close.
            }

            @Override
            public boolean requiresLayout() {
                return false;
            }
        };
        sumlog.addAppender(summary);
        asmlog.addAppender(failing);
        asmlog.setLevel(Level.DEBUG);
        try {
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new Assembler(xmir, temp.resolve("output"), false).assemble(),
                "Logging failure must propagate out of assemble()"
            );
        } finally {
            asmlog.removeAppender(failing);
            asmlog.setLevel(previous);
            sumlog.removeAppender(summary);
        }
        MatcherAssert.assertThat(
            "Summary must still be logged when per-file logging throws",
            logs.toString(),
            Matchers.containsString("Total 1 files were assembled")
        );
    }
}
