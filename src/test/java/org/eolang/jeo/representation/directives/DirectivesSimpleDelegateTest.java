/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesSimpleDelegate}.
 * @since 0.12.0
 */
final class DirectivesSimpleDelegateTest {

    @Test
    void generatesDirectivesWithBaseAndName() throws ImpossibleModificationException {
        final String base = "test-base";
        MatcherAssert.assertThat(
            "The generated XML should match the expected format",
            new Xembler(new DirectivesSimpleDelegate(base), new Transformers.Node()).xml(),
            Matchers.is(Matchers.equalTo(String.format("<o base=\"%s\" name=\"@\"/>", base)))
        );
    }
}
