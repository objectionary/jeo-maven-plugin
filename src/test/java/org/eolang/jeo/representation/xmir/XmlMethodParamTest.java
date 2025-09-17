/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeMethodParameter;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link XmlMethodParam}.
 * @since 0.6
 */
final class XmlMethodParamTest {

    @Test
    void convertsToBytecode() throws ImpossibleModificationException {
        final BytecodeMethodParameter expected = new BytecodeMethodParameter(
            1,
            "foo",
            Opcodes.ACC_STATIC,
            Type.INT_TYPE
        );
        MatcherAssert.assertThat(
            "Can't convert XML param to bytecode",
            new XmlMethodParam(
                new NativeXmlNode(new Xembler(expected.directives(new Format())).xml())
            ).bytecode(),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void parsesMethodParamWithoutName() throws ImpossibleModificationException {
        final BytecodeMethodParameter expected = new BytecodeMethodParameter(
            0,
            null,
            Opcodes.ACC_FINAL,
            Type.getType("Ljava/lang/String;")
        );
        MatcherAssert.assertThat(
            "Can't parse XML param without name",
            new XmlMethodParam(
                new JcabiXmlNode(
                    new Xembler(
                        new Directives(expected.directives(new Format()))
                            .xpath(".//o[@name='name']")
                            .remove()
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(expected)
        );
    }
}
