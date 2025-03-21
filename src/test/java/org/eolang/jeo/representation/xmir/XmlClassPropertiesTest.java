/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeClassProperties;
import org.eolang.jeo.representation.directives.DirectivesClassProperties;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link XmlClassProperties}.
 * @since 0.1
 */
final class XmlClassPropertiesTest {

    @Test
    void createsXmirWithCorrectProperties() {
        final int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | Opcodes.ACC_SUPER;
        final String signature = "Ljava/util/List<Ljava/lang/String;>;";
        final String supername = "some/custom/Supername";
        final String[] interfaces = {"java/util/List", "java/util/Collection"};
        MatcherAssert.assertThat(
            "We expect that the properties will be created correctly and contain the correct values",
            new XmlClass(
                "Language",
                new DirectivesClassProperties(
                    access,
                    signature,
                    supername,
                    interfaces
                )
            ).bytecode().properties(),
            Matchers.is(
                new BytecodeClassProperties(
                    access,
                    signature,
                    supername,
                    interfaces
                )
            )
        );
    }
}
