/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link ParallelTranslator}.
 *
 * @since 0.1.0
 */
final class ParallelTranslatorTest {

    @Test
    void savesXml(@TempDir final Path temp) throws IOException {
        final Path clazz = temp.resolve("Application.class");
        Files.write(
            clazz,
            new BytecodeObject(
                "org/eolang/jeo",
                new BytecodeClass("Application")
            ).bytecode().bytes()
        );
        new ParallelTranslator(ParallelTranslatorTest::transform)
            .apply(Stream.of(clazz))
            .forEach(ignored -> { });
        MatcherAssert.assertThat(
            "XML file was not saved",
            temp.resolve("Application.xmir").toFile(),
            FileMatchers.anExistingFile()
        );
    }

    @Test
    void overwritesXml(@TempDir final Path temp) throws IOException {
        final Path path = Paths.get(temp.toString(), "Application.class");
        Files.createDirectories(path.getParent());
        Files.write(
            path,
            new BytecodeObject(
                "org/eolang/jeo",
                new BytecodeClass("Application")
            ).bytecode().bytes()
        );
        final ParallelTranslator trans = new ParallelTranslator(ParallelTranslatorTest::transform);
        trans.apply(Stream.of(path)).forEach(ignored -> { });
        trans.apply(Stream.of(path)).forEach(ignored -> { });
        MatcherAssert.assertThat(
            "XML file was not successfully overwritten",
            temp.resolve("Application.xmir").toFile(),
            FileMatchers.anExistingFile()
        );
    }

    @Test
    void assemblesSuccessfully(@TempDir final Path temp) throws IOException {
        final String fake = "Fake";
        final Path path = temp.resolve("jeo")
            .resolve("xmir")
            .resolve(String.format("%s.xmir", fake));
        Files.createDirectories(path.getParent());
        Files.write(
            path,
            new BytecodeObject(
                "jeo/xmir",
                new BytecodeClass(fake)
            ).xml().toString().getBytes(StandardCharsets.UTF_8)
        );
        new ParallelTranslator(ParallelTranslatorTest::transform)
            .apply(Stream.of(path))
            .forEach(ignored -> { });
        MatcherAssert.assertThat(
            String.format(
                "Bytecode file was not saved for the representation with the name '%s'",
                fake
            ),
            temp.resolve("jeo")
                .resolve("xmir")
                .resolve("Fake.class")
                .toFile(),
            FileMatchers.anExistingFile()
        );
    }

    @Test
    void rejectsNegativeThreads() {
        MatcherAssert.assertThat(
            "The error must explain that only 0 or a positive number is allowed",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ParallelTranslator(ParallelTranslatorTest::transform, -1)
                    .apply(Stream.empty()),
                "A negative thread count must be rejected with a descriptive error"
            ).getMessage(),
            Matchers.containsString("0 or positive")
        );
    }

    @Test
    void stopsTranslationWhenWaitingThreadIsInterrupted(@TempDir final Path temp)
        throws InterruptedException {
        final CountDownLatch started = new CountDownLatch(1);
        final CountDownLatch release = new CountDownLatch(1);
        final CountDownLatch stopped = new CountDownLatch(1);
        final CountDownLatch completed = new CountDownLatch(1);
        final AtomicBoolean restored = new AtomicBoolean();
        final AtomicBoolean continued = new AtomicBoolean();
        final Thread caller = new Thread(
            () -> {
                try {
                    new ParallelTranslator(
                        path -> ParallelTranslatorTest.interruptibleTranslation(
                            path, started, release, stopped, continued
                        ),
                        1
                    ).apply(Stream.of(temp.resolve("input"))).forEach(ignored -> { });
                } catch (final IllegalStateException exception) {
                    restored.set(Thread.currentThread().isInterrupted());
                } finally {
                    completed.countDown();
                }
            }
        );
        caller.start();
        try {
            started.await(5L, TimeUnit.SECONDS);
            caller.interrupt();
            MatcherAssert.assertThat(
                "The interrupted call must wait until its worker stops",
                completed.await(5L, TimeUnit.SECONDS) && stopped.getCount() == 0L
                    && restored.get() && !continued.get()
            );
        } finally {
            release.countDown();
            caller.interrupt();
            caller.join(TimeUnit.SECONDS.toMillis(5L));
        }
    }

    private static Path interruptibleTranslation(
        final Path path, final CountDownLatch started, final CountDownLatch release,
        final CountDownLatch stopped, final AtomicBoolean continued
    ) {
        started.countDown();
        final Path result;
        try {
            release.await();
            continued.set(true);
            result = path;
        } catch (final InterruptedException exception) {
            stopped.countDown();
            Thread.currentThread().interrupt();
            throw new IllegalStateException(exception);
        }
        return result;
    }

    private static Path transform(final Path path) {
        final Path result;
        try {
            final String name = path.getFileName().toString();
            if (name.endsWith(".class")) {
                final Path target = path.resolveSibling(name.replace(".class", ".xmir"));
                Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
                result = target;
            } else if (name.endsWith(".xmir")) {
                final Path target = path.resolveSibling(name.replace(".xmir", ".class"));
                Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
                result = target;
            } else {
                throw new IllegalArgumentException(String.format("Unexpected file type: %s", name));
            }
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't transform file '%s'", path),
                exception
            );
        }
        return result;
    }
}
