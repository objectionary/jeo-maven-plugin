/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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
package it;

import com.jcabi.xml.XML;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Integration test that checks that the Java code is transpiled to EO code correctly.
 * @since 0.1
 */
final class JavaToEoTest {

    /**
     * Path to the EO resources.
     */
    private static final String EO_RESOURCES = "transpilation/eo";

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("arguments")
    void compilesJavaAndTranspilesBytecodeToEo(final Resource resource) {
        final String eolang = resource.eolang();
        final String java = resource.java();
        final XML actual = new BytecodeRepresentation(new JavaSourceClass(java).compile()).toEO();
        MatcherAssert.assertThat(
            String.format(
                "The transpiled EO code is not as expected, we compared the next files:%n%s%n%s%nReceived XML:%n%n%s%n",
                eolang,
                java,
                actual
            ),
            actual,
            Matchers.equalTo(new EoSource(eolang).parse())
        );
    }

    /**
     * Provides expected and actual resources for the test.
     * @return Stream of arguments.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> arguments() {
        try {
            return JavaToEoTest.resources(JavaToEoTest.EO_RESOURCES)
                .stream()
                .map(Arguments::of);
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't retrieve resources for the test from %s folder",
                    JavaToEoTest.EO_RESOURCES
                ),
                exception
            );
        }
    }

    /**
     * Retrieve resources from the folder.
     * @param path Path to the folder.
     * @return List of resources.
     * @throws IOException If something goes wrong.
     */
    private static List<Resource> resources(final String path) throws IOException {
        final List<Resource> res = new ArrayList<>(0);
        try (
            BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream(path),
                    StandardCharsets.UTF_8
                )
            )
        ) {
            String resource = br.readLine();
            while (resource != null) {
                if (resource.endsWith(".eo")) {
                    res.add(
                        new Resource(
                            path.substring(JavaToEoTest.EO_RESOURCES.length()),
                            resource.substring(0, resource.length() - 3)
                        )
                    );
                } else {
                    res.addAll(JavaToEoTest.resources(String.format("%s/%s", path, resource)));
                }
                resource = br.readLine();
            }
        }
        return res;
    }

    /**
     * Pair of resources for the test.
     * Can retrieve EO and Java files.
     * @since 0.1
     */
    private static final class Resource {

        /**
         * Subpath to the resource.
         */
        private final String subpath;

        /**
         * Filename of the resource.
         */
        private final String filename;

        /**
         * Constructor.
         * @param subpath Subpath to the resource.
         * @param filename Filename of the resource.
         */
        private Resource(final String subpath, final String filename) {
            this.subpath = subpath;
            this.filename = filename;
        }

        @Override
        public String toString() {
            return String.format("%s.eo and %1$s.java", this.filename);
        }

        private String eolang() {
            return String.format("transpilation/eo%s/%s.eo", this.subpath, this.filename);
        }

        private String java() {
            return String.format("transpilation/java%s/%s.java", this.subpath, this.filename);
        }
    }
}
