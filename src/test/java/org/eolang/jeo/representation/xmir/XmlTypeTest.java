/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;

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
                    "<o base='Q.jeo.type'><o base='Q.org.eolang.string'><o base='Q.org.eolang.bytes'>5A-</o></o></o>"
                )
            ).type(),
            Matchers.equalTo(Type.BOOLEAN_TYPE)
        );
    }
}
