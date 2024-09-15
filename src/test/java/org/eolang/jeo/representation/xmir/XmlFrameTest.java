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
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeFrame;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link XmlFrame}.
 *
 * @since 0.3
 */
final class XmlFrameTest {

    @Test
    void parsesRawXmirNode() {
        MatcherAssert.assertThat(
            "Parsed frame type is not correct.",
            new XmlFrame(
                "<?xml version='1.0' encoding='UTF-8'?>\n",
                "<o base='frame'>\n",
                " <o base='int' data='bytes'>FF FF FF FF FF FF FF FF</o>\n",
                " <o base='int' data='bytes'>00 00 00 00 00 00 00 02</o>\n",
                " <o base='tuple' star=''>\n",
                "  <o base='string' data='bytes'>6A 61 76 61 2F 6C 61 6E 67 2F 4F 62 6A 65 63 74</o>\n",
                "  <o base='int' data='bytes'>00 00 00 00 00 00 00 04</o>\n",
                " </o>\n",
                " <o base='int' data='bytes'>00 00 00 00 00 00 00 03</o>\n",
                " <o base='tuple' star=''>\n",
                "  <o base='string' data='bytes'>6A 61 76 61 2F 6C 61 6E 67 2F 53 74 72 69 6E 67</o>\n",
                "  <o base='int' data='bytes'>00 00 00 00 00 00 00 03</o>\n",
                " </o>\n",
                "</o>\n"
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeFrame(
                    Opcodes.F_NEW,
                    2,
                    new Object[]{"java/lang/Object", Opcodes.LONG},
                    3,
                    new Object[]{"java/lang/String", Opcodes.DOUBLE}
                )
            )
        );
    }
}
