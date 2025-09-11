/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Collections;
import org.eolang.jeo.representation.bytecode.BytecodeModule;
import org.eolang.jeo.representation.bytecode.BytecodeModuleExported;
import org.eolang.jeo.representation.bytecode.BytecodeModuleOpened;
import org.eolang.jeo.representation.bytecode.BytecodeModuleProvided;
import org.eolang.jeo.representation.bytecode.BytecodeModuleRequired;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlModule}.
 * @since 0.15.0
 */
final class XmlModuleTest {

    @Test
    void parsesModule() throws ImpossibleModificationException {
        final Format format = new Format();
        final BytecodeModule original = new BytecodeModule(
            "name",
            0,
            "0.1.0",
            "main",
            Collections.singletonList("org.eolang.jeo"),
            Collections.singletonList(new BytecodeModuleRequired("required", 0, "0.1.0")),
            Collections.singletonList(
                new BytecodeModuleExported("exported", 1, Collections.singletonList("m1"))
            ),
            Collections.singletonList(
                new BytecodeModuleOpened("opened", 2, Collections.singletonList("m2"))
            ),
            Collections.singletonList(
                new BytecodeModuleProvided("provided", Collections.singletonList("impl"))
            ),
            Collections.singletonList("used")
        );
        MatcherAssert.assertThat(
            "We expect to parse the same module from XML",
            new XmlModule(
                new JcabiXmlNode(new Xembler(original.directives(0, format)).xml())
            ).bytecode(),
            Matchers.equalTo(original)
        );
    }
}
