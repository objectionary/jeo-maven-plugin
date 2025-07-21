/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.directives.DirectivesSimpleDelegate;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlSimpleDelegate}.
 * @since 0.12.0
 */
final class XmlSimpleDelegateTest {

    @Test
    void parsesBaseAttribute() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Expected the base attribute to be 'test-base'",
            new XmlSimpleDelegate(
                new JcabiXmlNode(new Xembler(new DirectivesSimpleDelegate("test-base")).xml())
            ).base(),
            Matchers.equalTo("test-base")
        );
    }
}
