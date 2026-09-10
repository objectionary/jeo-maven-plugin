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
 * @since 0.15.4
 */
final class DisassembleMojoTest {

    @Test
    void rejectsUnknownModeBeforeStartingWork() throws Exception {
        final DisassembleMojo mojo = new DisassembleMojo();
        DisassembleMojoTest.field("mode").set(mojo, "Debag");
        DisassembleMojoTest.field("threads").setInt(mojo, 0);
        MatcherAssert.assertThat(
            "Unknown mode was not rejected before disassembly setup",
            Assertions.assertThrows(MojoExecutionException.class, mojo::execute).getMessage(),
            Matchers.containsString("Unknown disassemble mode: Debag")
        );
    }

    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    private static Field field(final String name) throws NoSuchFieldException {
        final Field field = DisassembleMojo.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
}
