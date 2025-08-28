/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.eolang.jeo.representation.bytecode.InnerClass;
import org.eolang.jeo.representation.directives.DirectivesAttribute;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlAttribute}.
 * @since 0.6
 */
final class XmlAttributeTest {

    @Test
    void convertsToDomainInnerClass() throws ImpossibleModificationException {
        final BytecodeAttribute expected = new InnerClass("name", "outer", "inner", 0);
        MatcherAssert.assertThat(
            "We expect the attribute to be converted to a correct  domain attribute (inner class)",
            new XmlAttribute(new Xembler(expected.directives(new Format())).xml()).attribute(),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void throwsExceptionIfCannotIdentifyAttribute() {
        MatcherAssert.assertThat(
            "We expect an exception message be understandable and clear",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new XmlAttribute(
                    new Xembler(new DirectivesAttribute("unknown")).xmlQuietly()
                ).attribute(),
                "We expect an exception to be thrown if the attribute cannot be identified"
            ).getMessage(),
            Matchers.equalTo("Unknown attribute base 'Q.jeo.unknown'")
        );
    }
}
