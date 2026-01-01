/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlAnnotations}.
 * @since 0.6
 */
final class XmlAnnotationsTest {

    @Test
    void parsesXmirAnnotations() throws ImpossibleModificationException {
        final BytecodeAnnotations expected = new BytecodeAnnotations(
            new BytecodeAnnotation("java/lang/Override", true)
        );
        MatcherAssert.assertThat(
            "We expect that XMIR annotations are parsed correctly",
            new XmlAnnotations(
                new NativeXmlNode(
                    new Xembler(expected.directives(new Format(), "")).xml()
                )
            ).bytecode(),
            Matchers.equalTo(expected)
        );
    }
}
