/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesHandle}.
 *
 * @since 0.3
 */
final class DirectivesHandleTest {

    @Test
    void convertsHandleToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new DirectivesHandle(
                new Handle(Opcodes.H_INVOKESTATIC, "java/lang/Math", "max", "(II)I", false)
            )
        ).xml();
        MatcherAssert.assertThat(
            String.format("Can't convert Handle to DirectivesHandle. Expected XML:%n%s%n", xml),
            xml,
            XhtmlMatchers.hasXPaths(
                "/o[contains(@base,'handle')]",
                "/o[contains(@base,'handle')]/o[contains(@base,'int')]",
                "/o[contains(@base,'handle')]/o[contains(@base,'string')]"
            )
        );
    }
}
