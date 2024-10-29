/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.eolang.jeo.representation.bytecode.BytecodeField;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesField}.
 * @since 0.3
 */
final class DirectivesFieldTest {

    @Test
    void convertsDefaultFieldToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(new DirectivesField()).xml();
        MatcherAssert.assertThat(
            String.format(
                "Incorrect transformation of default field to directives, received invalid XMIR: %n%s",
                xml
            ),
            xml,
            XhtmlMatchers.hasXPaths(
                "/o[contains(@base,'field') and contains(@name,'unknown')]",
                "/o/o[@name='access-unknown']/o[contains(text(),'01')]",
                "/o/o[@name='descriptor-unknown']/o[text()='49']",
                "/o/o[@name='signature-unknown']",
                "/o/o[contains(@base,'int') and @name='value-unknown']/o[contains(text(),'0')]"
            )
        );
    }

    @Test
    void convertsLongFieldToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new DirectivesField(
                Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                "serialVersionUID",
                "J",
                "",
                7_099_057_708_183_571_937L
            )
        ).xml();
        MatcherAssert.assertThat(
            String.format(
                "Incorrect transformation of long field to directives, received invalid XMIR: %n%s",
                xml
            ),
            xml,
            XhtmlMatchers.hasXPaths(
                "/o[contains(@base,'field') and contains(@name,'serialVersionUID')]",
                "/o/o[@name='access-serialVersionUID']/o[contains(text(),'1A')]",
                "/o/o[@name='descriptor-serialVersionUID']/o[text()='4A']",
                "/o/o[@name='signature-serialVersionUID']",
                "/o/o[@name='value-serialVersionUID']/o[text()='62 84 EB 5F 88 47 CD E1']"
            )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Φ", "Ψ", "Ω", "Δ", "Σ", "Θ", "Λ", "Ξ", "Π", "Υ", "\u03A3", "\u03A6", "\u03A8", "\u03A9"
    })
    void convertsDirectivesFieldWithUnicodeName(
        final String original
    ) throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect the field name with unicode characters to be successfully converted to directives",
            new Xembler(
                new BytecodeField(
                    original,
                    "I",
                    "",
                    0,
                    Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL
                ).directives()
            ).xml(),
            XhtmlMatchers.hasXPaths(
                String.format(
                    "/o[contains(@base,'field') and contains(@name,'%s')]",
                    original
                )
            )
        );
    }
}
