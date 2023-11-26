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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link org.eolang.jeo.representation.xmir.XmlClassProperties}.
 * @since 0.1
 */
class XmlClassPropertiesTest {

    @Test
    void retrievesAccessModifier() {
        final String xml =
            String.join(
                "\n",
                "<o abstract='' name='Language'>",
                "  <o base='int' data='bytes' name='access'>00 00 00 00 00 00 04 21</o>",
                "  <o base='string' data='bytes' name='supername'>",
                "6A 61 76 61 2F 6C 61 6E 67 2F 4F 62 6A 65 63 74</o>",
                "  <o base='tuple' data='tuple' name='interfaces'/>",
                "</o>"
            );
        final int actual = new XmlClassProperties(xml).access();
        final int expected = Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | Opcodes.ACC_SUPER;
        MatcherAssert.assertThat(
            String.format(
                "Can't retrieve access modifier correctly, expected %d (public abstract class), got %d",
                expected,
                actual
            ),
            actual,
            Matchers.is(expected)
        );
    }
}
