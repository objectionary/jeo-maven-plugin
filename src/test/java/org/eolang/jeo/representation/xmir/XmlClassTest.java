/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link XmlClass}.
 *
 * @since 0.1
 */
final class XmlClassTest {

    @Test
    void createsByName() {
        final String expected = "FooClass";
        final XmlClass klass = new XmlClass(expected);
        MatcherAssert.assertThat(
            String.format(
                "%s should create a class with name %s%n",
                klass,
                expected
            ),
            klass.bytecode(),
            Matchers.equalTo(new BytecodeClass(expected))
        );
    }
}
