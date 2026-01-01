/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.eolang.jeo.representation.bytecode.BytecodeRecordComponent;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotations;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test for {@link XmlRecordComponents}.
 * @since 0.15.0
 */
final class XmlRecordComponentsTest {

    @Test
    void parsesRecordComponents() throws ImpossibleModificationException {
        final BytecodeAttribute.RecordComponents original = new BytecodeAttribute.RecordComponents(
            new BytecodeRecordComponent(
                "name",
                "Ljava/lang/String;",
                null,
                new BytecodeAnnotations(),
                new BytecodeTypeAnnotations()
            )
        );
        MatcherAssert.assertThat(
            "We expect the record components to be parsed correctly",
            new XmlRecordComponents(
                new JcabiXmlNode(new Xembler(original.directives(0, new Format())).xml())
            ).bytecode(),
            Matchers.equalTo(original)
        );
    }
}
