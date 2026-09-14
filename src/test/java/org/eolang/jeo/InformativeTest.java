/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Informative}.
 *
 * @since 0.14.0
 */
final class InformativeTest {

    @Test
    void transformsSuccessfullyWhenOriginalTransformationSucceeds() {
        final byte[] expected = "transformed".getBytes(StandardCharsets.UTF_8);
        MatcherAssert.assertThat(
            "We expect the transformation to delegate successfully",
            new Informative(
                new Transformation() {
                    @Override
                    public Path source() {
                        return Paths.get("source.txt");
                    }

                    @Override
                    public Path target() {
                        return Paths.get("target.txt");
                    }

                    @Override
                    public byte[] transform() {
                        return expected;
                    }
                }
            ).transform(),
            Matchers.equalTo(expected)
        );
    }

    @Test
    @SuppressWarnings("PMD.AvoidThrowingNullPointerException")
    void throwsInformativeExceptionWhenOriginalTransformationFails() {
        MatcherAssert.assertThat(
            "Exception message should contain informative details",
            Assertions.assertThrows(
                IllegalStateException.class,
                new Informative(
                    new Transformation() {
                        @Override
                        public Path source() {
                            return Paths.get("source.txt");
                        }

                        @Override
                        public Path target() {
                            return Paths.get("target.txt");
                        }

                        @Override
                        public byte[] transform() {
                            throw new NullPointerException("Original failure");
                        }
                    }
                )::transform
            ).getMessage(),
            Matchers.containsString(
                "Failed to transform source.txt to target.txt: Original failure"
            )
        );
    }
}
