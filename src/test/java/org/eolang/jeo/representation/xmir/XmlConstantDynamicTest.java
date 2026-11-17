/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.directives.DirectivesConstantDynamic;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlConstantDynamic}.
 *
 * @since 0.18.0
 */
final class XmlConstantDynamicTest {

    @Test
    void convertsToConstantDynamicObject()
        throws ImpossibleModificationException {
        final ConstantDynamic dynamic = new ConstantDynamic(
            "constName",
            "Ljava/lang/String;",
            new Handle(
                Opcodes.H_INVOKESTATIC, "owner", "name", "()V", false
            ),
            new Object[]{"argument"}
        );
        MatcherAssert.assertThat(
            "Can't convert XML constant dynamic to ASM object",
            new XmlConstantDynamic(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesConstantDynamic(
                            0, new Format(), dynamic
                        )
                    ).xml()
                )
            ).constant(),
            Matchers.equalTo(dynamic)
        );
    }

    @Test
    void refusesConstantDynamicWithZeroOperands() {
        MatcherAssert.assertThat(
            "The message must report that node has no children",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new XmlConstantDynamic(
                    new NativeXmlNode("<o base='Q.jeo.constant-dynamic'/>")
                ).constant(),
                "A constant dynamic with zero operands must be refused"
            ).getMessage(),
            Matchers.containsString("doesn't have any children")
        );
    }

    @Test
    void refusesConstantDynamicWithOneOperand() {
        MatcherAssert.assertThat(
            "The message must report 1 operand present and 4 expected",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new XmlConstantDynamic(
                    new NativeXmlNode(
                        String.join(
                            "",
                            "<o base='Q.jeo.constant-dynamic' name='c0'>",
                            "<o base='Q.org.eolang.string' name='n'>",
                            "66-6F-6F</o>",
                            "</o>"
                        )
                    )
                ).constant(),
                "A constant dynamic with 1 operand must be refused"
            ).getMessage(),
            Matchers.allOf(
                Matchers.containsString("has 1 operands"),
                Matchers.containsString("4 expected")
            )
        );
    }

    @Test
    void refusesConstantDynamicWithTwoOperands() {
        MatcherAssert.assertThat(
            "The message must report 2 operands present and 4 expected",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new XmlConstantDynamic(
                    new NativeXmlNode(
                        String.join(
                            "",
                            "<o base='Q.jeo.constant-dynamic' name='c0'>",
                            "<o base='Q.org.eolang.string' name='n'>",
                            "66-6F-6F</o>",
                            "<o base='Q.org.eolang.string' name='d'>",
                            "49</o>",
                            "</o>"
                        )
                    )
                ).constant(),
                "A constant dynamic with 2 operands must be refused"
            ).getMessage(),
            Matchers.allOf(
                Matchers.containsString("has 2 operands"),
                Matchers.containsString("4 expected")
            )
        );
    }

    @Test
    void refusesConstantDynamicWithThreeOperands() {
        MatcherAssert.assertThat(
            "The message must report 3 operands present and 4 expected",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new XmlConstantDynamic(
                    new NativeXmlNode(
                        String.join(
                            "",
                            "<o base='Q.jeo.constant-dynamic' name='c0'>",
                            "<o base='Q.org.eolang.string' name='n'>",
                            "66-6F-6F</o>",
                            "<o base='Q.org.eolang.string' name='d'>",
                            "49</o>",
                            "<o base='Q.jeo.handle' name='h'>",
                            "<o base='Q.org.eolang.number' name='t'>",
                            "00-00-00-00-00-00-F0-3F</o>",
                            "</o>",
                            "</o>"
                        )
                    )
                ).constant(),
                "A constant dynamic with 3 operands must be refused"
            ).getMessage(),
            Matchers.allOf(
                Matchers.containsString("has 3 operands"),
                Matchers.containsString("4 expected")
            )
        );
    }
}
