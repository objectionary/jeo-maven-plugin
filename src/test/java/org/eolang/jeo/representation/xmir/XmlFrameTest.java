/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeFrame;
import org.eolang.jeo.representation.directives.DirectivesFrameValues;
import org.eolang.jeo.representation.directives.DirectivesJeoObject;
import org.eolang.jeo.representation.directives.DirectivesValue;
import org.eolang.jeo.representation.directives.Format;
import org.eolang.jeo.representation.directives.NumName;
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
            2,
            new Object[]{"java/lang/String", Opcodes.DOUBLE}
        );
        MatcherAssert.assertThat(
            "Parsed frame type is not correct.",
            new XmlFrame(new Xembler(expected.directives(0, new Format())).xml()).bytecode(),
            Matchers.equalTo(expected)
        );
    }

    /**
     * This test verifies that we support backward compatibility with the old frame representation.
     * The new frame representation was introduced in this
     * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1215">issue</a>
     * @throws ImpossibleModificationException If the Xembler fails to modify the XML
     */
    @Test
    void parsesFullFrameNode() throws ImpossibleModificationException {
        final Format format = new Format();
        MatcherAssert.assertThat(
            "We expect XmlFrame to parse old-style frame representation for backward compatibility",
            new XmlFrame(
                new Xembler(
                    new DirectivesJeoObject(
                        "frame",
                        new NumName("f", 0).toString(),
                        new DirectivesValue(format, "type", Opcodes.F_CHOP),
                        new DirectivesValue(format, "nlocal", 0),
                        new DirectivesFrameValues(format, "locals"),
                        new DirectivesValue(format, "nstack", 3),
                        new DirectivesFrameValues(format, "stack", null, null, null)
                    )
                ).xml()
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeFrame(
                    Opcodes.F_CHOP,
                    0,
                    new Object[0],
                    3,
                    new Object[]{null, null, null}
                )
            )
        );
    }
}
