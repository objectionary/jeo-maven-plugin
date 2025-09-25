/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package benchmark;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import org.eolang.jeo.Assembler;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Assemble benchmark.
 * @since 0.8
 * @checkstyle DesignForExtensionCheck (500 lines)
 */
@Fork(1)
@Warmup(iterations = 1, time = 2)
@Measurement(iterations = 1, time = 3)
@State(Scope.Benchmark)
@SuppressWarnings("PMD.JUnit4TestShouldUseAfterAnnotation")
public class AssembleBenchmark {

    /**
     * Temporary directory.
     */
    private Path dir;

    /**
     * Assembler.
     */
    private Assembler assembler;

    /**
     * This method is used to run the benchmark from IDE.
     * Don't remove it.
     * @param args Arguments.
     * @throws RunnerException If something goes wrong.
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static void main(final String[] args) throws RunnerException {
        new Runner(
            new OptionsBuilder()
                .include(AssembleBenchmark.class.getSimpleName())
                .build()
        ).run();
    }

    @Setup(Level.Trial)
    public void init() throws IOException {
        this.dir = Files.createTempDirectory("assemble");
        final Path input = this.dir.resolve("input");
        this.dir.toFile().deleteOnExit();
        Files.createDirectories(input);
        Files.write(input.resolve("test.xmir"), new Representation().disassemble());
        this.assembler = new Assembler(input, this.dir, false);
    }

    @Benchmark
    public void assemble() {
        this.assembler.assemble();
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        try (Stream<Path> files = Files.walk(this.dir).sorted(Comparator.reverseOrder())) {
            files.map(Path::toFile).forEach(File::delete);
        } catch (final IOException ioex) {
            throw new IllegalStateException("Failed to delete assemble temporary directory", ioex);
        }
    }
}
