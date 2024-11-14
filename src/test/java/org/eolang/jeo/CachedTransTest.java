/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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
package org.eolang.jeo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Cached transformation test.
 * Test cases for {@link CachedTrans}.
 * @since 0.6
 */
final class CachedTransTest {

    @Test
    void skipsOriginalTransformationSinceAlreadyTransformed(@TempDir final Path temp) {
        final MockTrans mock = new MockTrans(temp);
        mock.createFrom();
        mock.createTo();
        MatcherAssert.assertThat(
            "Cached transformation should skip original transformation and return the cached result",
            new String(new CachedTrans(mock).transform(), StandardCharsets.UTF_8),
            Matchers.equalTo(MockTrans.OLD_TO)
        );
    }

    @Test
    void performsTransformationSinceModified(@TempDir final Path temp) {
        final MockTrans mock = new MockTrans(temp);
        mock.createTo();
        mock.createFrom();
        MatcherAssert.assertThat(
            "Cached transformation should perform original transformation since the source file is modified",
            new String(new CachedTrans(mock).transform(), StandardCharsets.UTF_8),
            Matchers.equalTo(MockTrans.PERFORMED)
        );
    }

    @Test
    void performsTransformationSinceNotYetTransformed(@TempDir final Path temp) {
        final MockTrans mock = new MockTrans(temp);
        mock.createFrom();
        MatcherAssert.assertThat(
            "Cached transformation should perform original transformation and return the result",
            new String(new CachedTrans(mock).transform(), StandardCharsets.UTF_8),
            Matchers.equalTo(MockTrans.PERFORMED)
        );
    }

    private static class MockTrans implements FileTransformation {

        /**
         * Transformation is performed.
         */
        private static String PERFORMED = "Transformation is performed";

        /**
         * Old content of 'to' file.
         */
        private static String OLD_TO = "Old content of 'to' file";

        /**
         * Temporary directory.
         */
        private final Path temp;

        /**
         * Constructor.
         * @param temp Temporary directory.
         */
        MockTrans(final Path temp) {
            this.temp = temp;
        }

        @Override
        public Path from() {
            return this.temp.resolve("from.xmir");
        }

        @Override
        public Path to() {
            return this.temp.resolve("to.xmir");
        }

        @Override
        public byte[] transform() {
            return MockTrans.PERFORMED.getBytes(StandardCharsets.UTF_8);
        }

        /**
         * Create 'from' file.
         */
        void createFrom() {
            this.create(this.from(), "Old content of 'from' file".getBytes(StandardCharsets.UTF_8));
        }

        /**
         * Create 'to' file.
         */
        void createTo() {
            this.create(this.to(), MockTrans.OLD_TO.getBytes(StandardCharsets.UTF_8));
        }

        /**
         * Create file.
         * @param path Path to the file.
         * @param content Content of the file.
         */
        private void create(final Path path, byte[] content) {
            try {
                Files.write(path, content);
            } catch (final IOException exception) {
                throw new IllegalStateException(
                    String.format(
                        "Failed to create file '%s'",
                        this.from()
                    ),
                    exception
                );
            }
        }
    }
}