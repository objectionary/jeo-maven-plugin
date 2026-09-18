/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlEnclosingMethod}.
 *
 * @since 0.15.0
 */
final class XmlEnclosingMethodTest {

    @Test
    void complainsAboutTheMissingDescriptor() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "The message names the owner instead of the descriptor",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new XmlEnclosingMethod(
                    new XmlJeoObject(
                        new JcabiXmlNode(
                            new XMLDocument(
                                new Xembler(
                                    new Directives().xpath("/o/o[last()]").remove()
                                ).apply(
                                    new XMLDocument(
                                        new Xembler(
                                            new BytecodeAttribute.EnclosingMethod(
                                                "Owner", "method", "()V"
                                            ).directives(0, new Format())
                                        ).xml()
                                    ).inner()
                                )
                            ).toString()
                        )
                    )
                ).attribute(),
                "An attribute without a descriptor should be refused"
            ).getMessage(),
            Matchers.containsString("descriptor")
        );
    }
}
