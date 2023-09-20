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
 *
 * @since 0.1
 */
final class JavaToEoTest {

    private final static String EO_RESOURCES = "transpilation/eo";

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("expectedAndActual")
    void compilesJavaAndTranspilesBytecodeToEo(final Resource resource) {
        final String eo = resource.eo();
        final String java = resource.java();
        MatcherAssert.assertThat(
            String.format(
                "The transpiled EO code is not as expected, we compared the next files:%n%s%n%s",
                eo,
                java
            ),
            new BytecodeRepresentation(new JavaSourceClass(java).compile()).toEO(),
            Matchers.equalTo(new EoSource(eo).parse())
        );
    }

    /**
     * Provides expected and actual resources for the test.
     * @return Stream of arguments.
     */
    private static Stream<Arguments> expectedAndActual() {
        try {
            return JavaToEoTest.resources(JavaToEoTest.EO_RESOURCES)
                .stream()
                .map(Arguments::of);
        } catch (IOException ex) {
            throw new IllegalStateException(
                String.format(
                    "Can't retrieve resources for the test from %s folder",
                    JavaToEoTest.EO_RESOURCES
                ),
                ex
            );
        }
    }

    /**
     * Retrieve resources from the folder.
     * @param path Path to the folder.
     * @return List of resources.
     * @throws IOException If something goes wrong.
     */
    private static List<Resource> resources(String path) throws IOException {
        List<Resource> res = new ArrayList<>(0);
        try (
            BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    JavaSourceClass.class.getClassLoader().getResourceAsStream(path),
                    StandardCharsets.UTF_8
                )
            )
        ) {
            String resource;
            while ((resource = br.readLine()) != null) {
                if (!resource.endsWith(".eo")) {
                    res.addAll(JavaToEoTest.resources(String.format("%s/%s", path, resource)));
                } else {
                    res.add(
                        new Resource(
                            path.substring(JavaToEoTest.EO_RESOURCES.length()),
                            resource.substring(0, resource.length() - 3)
                        )
                    );
                }
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

        private String eo() {
            return String.format("transpilation/eo%s/%s.eo", this.subpath, this.filename);
        }

        private String java() {
            return String.format("transpilation/java%s/%s.java", this.subpath, this.filename);
        }

        @Override
        public String toString() {
            return String.format("%s.eo and %1$s.java", this.filename);
        }
    }
}
