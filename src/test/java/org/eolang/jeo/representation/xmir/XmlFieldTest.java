/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.directives.DirectivesField;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlField}.
 *
 * @since 0.3
 */
final class XmlFieldTest {

    @Test
    void parsesXmirSuccessfully() throws ImpossibleModificationException {
        final int access = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
        final String name = "serialVersionUID";
        final String descriptor = "J";
        final long value = 7099057708183571937L;
        final XmlField field = new XmlField(
            new XmlNode(
                new Xembler(
                    new DirectivesField(
                        access,
                        name,
                        descriptor,
                        "",
                        value
                    )
                ).xml()
            )
        );
        MatcherAssert.assertThat(
            String.format(
                "Failed to parse XMIR field. Expected name '%s', actual '%s'. Expected descriptor '%s', actual '%s'. Expected value '%s', actual '%s'",
                name,
                field.name(),
                descriptor,
                field.descriptor(),
                value,
                field.value()
            ),
            name.equals(field.name())
                && descriptor.equals(field.descriptor())
                && field.value().equals(value)
        );
    }

}