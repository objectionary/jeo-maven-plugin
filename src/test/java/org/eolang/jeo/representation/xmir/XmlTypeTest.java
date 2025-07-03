/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.directives.DirectivesType;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import org.xembly.Xembler;

/**
 * Test for {@link XmlType}.
 * @since 0.11.0
 */
final class XmlTypeTest {

    @Test
    void parsesBooleanType() {
        MatcherAssert.assertThat(
            "We expect the type to be parsed correctly",
            new XmlType(
                new JcabiXmlNode(
                    new Xembler(
                        new DirectivesType(Type.BOOLEAN_TYPE)
                    ).xmlQuietly()
                )
            ).type(),
            Matchers.equalTo(Type.BOOLEAN_TYPE)
        );
    }
}
