/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Collections;
import org.eolang.jeo.representation.directives.DirectivesAbsractObject;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlAbstractObject}.
 * @since 0.11.0
 */
final class XmlAbstractObjectTest {

    @Test
    void parsesBaseOfAbstractObject() {
        final String base = "object-type";
        MatcherAssert.assertThat(
            "We expect the abstract object to save base as a first parameter",
            new XmlAbstractObject(
                new JcabiXmlNode(
                    new Xembler(
                        new DirectivesAbsractObject(
                            new Format(),
                            base,
                            "object-as",
                            "object-name",
                            Collections.emptyList()
                        )
                    ).xmlQuietly()
                )
            ).base().orElseThrow(AssertionError::new),
            Matchers.equalTo(base)
        );
    }
}
