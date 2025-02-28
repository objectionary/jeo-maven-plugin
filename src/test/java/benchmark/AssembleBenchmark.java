/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package benchmark;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;
import org.eolang.jeo.Assembler;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.bytecode.Bytecode;
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
    public static void main(String[] args) throws RunnerException {
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
        Files.write(
            input.resolve("test.xmir"), this.disassemble().getBytes(StandardCharsets.UTF_8));
        this.assembler = new Assembler(input, this.dir);
    }

    @Benchmark
    public void assemble() {
        this.assembler.assemble();
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        try (Stream<Path> files = Files.walk(this.dir).sorted(Comparator.reverseOrder())) {
            files.map(Path::toFile).forEach(File::delete);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to delete temporary directory", ex);
        }
    }

    /**
     * Disassemble Integer class.
     * @return Disassembled Integer class.
     */
    private String disassemble() {
        final InputStream stream = Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream(
                String.format(
                    "%s.class",
                    Integer.class.getName().replace('.', '/')
                )
            );
        final byte[] bytes = this.bytes(
            Optional.ofNullable(
                stream
            ).orElseThrow(() -> new IllegalStateException("Failed to load Integer class"))
        );
        return new BytecodeRepresentation(new Bytecode(bytes)).toEO().toString();
    }

    /**
     * Read all bytes from input stream.
     * @param stream Input stream.
     * @return Bytes.
     */
    private byte[] bytes(final InputStream stream) {
        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nread;
            byte[] data = new byte[stream.available()];
            while ((nread = stream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nread);
            }
            return buffer.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read all bytes from input stream", ex);
        }
    }
}
