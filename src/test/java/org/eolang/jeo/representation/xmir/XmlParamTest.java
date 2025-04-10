/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameter;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link XmlParam}.
 * @since 0.6
 */
final class XmlParamTest {

    @Test
    void convertsToBytecode() throws ImpossibleModificationException {
        final BytecodeMethodParameter expected = new BytecodeMethodParameter(
            1,
            "foo",
            Opcodes.ACC_STATIC,
            Type.INT_TYPE,
            new BytecodeAnnotations(new BytecodeAnnotation("Ljava/lang/Deprecated;", true))
        );
        MatcherAssert.assertThat(
            "Can't convert XML param to bytecode",
            new XmlParam(
                new NativeXmlNode(new Xembler(expected.directives()).xml())
            ).bytecode(),
            Matchers.equalTo(expected)
        );
    }
}
