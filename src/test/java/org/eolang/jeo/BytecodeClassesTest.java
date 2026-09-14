/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link BytecodeClasses}.
 *
 * @since 0.14.0
 */
final class BytecodeClassesTest {

    @Test
    void convertsToString() {
        final Path path = Paths.get("src/test/resources/bytecode-classes");
        MatcherAssert.assertThat(
            "BytecodeClasses toString() should return the correct path",
            new BytecodeClasses(path).toString(),
            Matchers.equalTo(path.toString())
        );
    }

    @Test
    void throwsDescriptiveExceptionWhenConvertingNullInputToString() {
        MatcherAssert.assertThat(
            "BytecodeClasses toString() should describe the missing classes directory",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new BytecodeClasses(null).toString()
            ).getMessage(),
            Matchers.containsString("The classes directory is not set")
        );
    }

    @Test
    void throwsDescriptiveExceptionWhenGettingRootFromNullInput() {
        MatcherAssert.assertThat(
            "BytecodeClasses root() should describe the missing classes directory",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new BytecodeClasses(null).root()
            ).getMessage(),
            Matchers.containsString("The classes directory is not set")
        );
    }

    @Test
    void doesNotThrowExceptionOnAbsentDirectory() {
        MatcherAssert.assertThat(
            "BytecodeClasses should handle empty directories without throwing exceptions",
            new BytecodeClasses(Paths.get("/dev/null/absent")).all().count(),
            Matchers.equalTo(0L)
        );
    }
}
