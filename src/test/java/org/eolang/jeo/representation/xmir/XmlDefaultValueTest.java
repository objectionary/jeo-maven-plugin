/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeDefaultValue;
import org.eolang.jeo.representation.bytecode.BytecodePlainAnnotationValue;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link XmlDefaultValue}.
 * @since 0.15.0
 */
final class XmlDefaultValueTest {

    @Test
    void parsesDefaultValue() throws ImpossibleModificationException {
        final BytecodeDefaultValue original = new BytecodeDefaultValue(
            new BytecodePlainAnnotationValue("name", "value")
        );
        final String xml = new Xembler(original.directives(new Format())).xml();
        MatcherAssert.assertThat(
            "We expect the annotation default value to survive the XMIR round-trip",
            new XmlDefaultValue(new NativeXmlNode(xml)).bytecode().orElseThrow(),
            Matchers.equalTo(original)
        );
    }
}
