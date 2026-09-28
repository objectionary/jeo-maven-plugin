/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.lang.reflect.Field;
import org.apache.maven.plugin.MojoExecutionException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link DisassembleMojo}.
 *
 * @since 0.15
 */
final class DisassembleMojoTest {

    @Test
    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    void rejectsUnknownModeBeforeAnyWork() throws Exception {
        final DisassembleMojo mojo = new DisassembleMojo();
        final Field mode = DisassembleMojo.class.getDeclaredField("mode");
        mode.setAccessible(true);
        mode.set(mojo, "Debag");
        MatcherAssert.assertThat(
            "An unknown mode must be rejected as a configuration error before disassembling",
            Assertions.assertThrows(MojoExecutionException.class, mojo::execute).getMessage(),
            Matchers.containsString("Unknown disassemble mode: Debag")
        );
    }
}
