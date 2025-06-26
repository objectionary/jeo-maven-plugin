/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import com.jcabi.xml.XML;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.representation.asm.DisassembleMode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link BytecodeRepresentation} omitComments functionality.
 * @since 0.9.0
 */
final class BytecodeRepresentationOmitCommentsTest {

    @Test
    void includesCommentsByDefault() {
        final XML xml = new BytecodeRepresentation(
            new ResourceOf("MethodByte.class")
        ).toEO(DisassembleMode.SHORT);
        MatcherAssert.assertThat(
            "Should include comments by default",
            xml.toString(),
            Matchers.containsString("<!--")
        );
    }

    @Test
    void omitsCommentsWhenRequested() {
        final XML xml = new BytecodeRepresentation(
            new ResourceOf("MethodByte.class")
        ).toEO(DisassembleMode.SHORT, true);
        MatcherAssert.assertThat(
            "Should omit comments when requested",
            xml.toString(),
            Matchers.not(Matchers.containsString("<!--"))
        );
    }

    @Test
    void includesCommentsWhenExplicitlyNotOmitted() {
        final XML xml = new BytecodeRepresentation(
            new ResourceOf("MethodByte.class")
        ).toEO(DisassembleMode.SHORT, false);
        MatcherAssert.assertThat(
            "Should include comments when explicitly not omitted",
            xml.toString(),
            Matchers.containsString("<!--")
        );
    }
}