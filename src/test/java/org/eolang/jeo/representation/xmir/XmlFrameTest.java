/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeFrame;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlFrame}.
 *
 * @since 0.3
 */
final class XmlFrameTest {

    @Test
    void parsesRawXmirNode() throws ImpossibleModificationException {
        final BytecodeFrame expected = new BytecodeFrame(
            Opcodes.F_NEW,
            2,
            new Object[]{"java/lang/Object", Opcodes.LONG},
            3,
            new Object[]{"java/lang/String", Opcodes.DOUBLE}
        );
        MatcherAssert.assertThat(
            "Parsed frame type is not correct.",
            new XmlFrame(new Xembler(expected.directives()).xml()).bytecode(),
            Matchers.equalTo(expected)
        );
    }
}
