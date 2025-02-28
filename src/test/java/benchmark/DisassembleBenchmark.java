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
import org.eolang.jeo.Disassembler;
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

@Fork(1)
@Warmup(iterations = 1, time = 2)
@Measurement(iterations = 1, time = 3)
@State(Scope.Benchmark)
public class DisassembleBenchmark {

    /**
     * Temporary directory.
     */
    private Path dir;

    /**
     * Assembler.
     */
    private Disassembler disassembler;

    /**
     * This method is used to run the benchmark from IDE.
     * Don't remove it.
     * @param args Arguments.
     * @throws RunnerException If something goes wrong.
     */
    public static void main(String[] args) throws RunnerException {
        new Runner(
            new OptionsBuilder()
                .include(DisassembleBenchmark.class.getSimpleName())
                .build()
        ).run();
    }

    @Setup(Level.Trial)
    public void init() throws IOException {
        this.dir = Files.createTempDirectory("disassemble");
        final Path input = this.dir.resolve("input");
        this.dir.toFile().deleteOnExit();
        Files.createDirectories(input);
        Files.write(input.resolve("X.class"), new Representation().bytecode());
        this.disassembler = new Disassembler(input, this.dir);
    }

    @Benchmark
    public void disassemble() {
        this.disassembler.disassemble();
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        try (Stream<Path> files = Files.walk(this.dir).sorted(Comparator.reverseOrder())) {
            files.map(Path::toFile).forEach(File::delete);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to delete disassemble temporary directory", ex);
        }
    }
}
