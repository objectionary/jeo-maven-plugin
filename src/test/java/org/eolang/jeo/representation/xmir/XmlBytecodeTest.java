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

/**
 * Test case for {@link XmlBytecode}.
 *
 * @since 0.1
 */
class XmlBytecodeTest {


    @Test
    void convertsGenericsMethodIntoBytecode() {
        MatcherAssert.assertThat(
            "Can't convert generics method into bytecode",
            new XmlBytecode("<program dob=\"2023-11-27T12:58:31.938342Z\"\n"
                + "         ms=\"1701089911938\"\n"
                + "         name=\"Application$TimeLog\"\n"
                + "         revision=\"0.0.0\"\n"
                + "         time=\"2023-11-27T12:58:31.938342Z\"\n"
                + "         version=\"0.0.0\">\n"
                + "   <listing/>\n"
                + "   <errors/>\n"
                + "   <sheets/>\n"
                + "   <license/>\n"
                + "   <metas>\n"
                + "      <meta>\n"
                + "         <head>package</head>\n"
                + "         <tail>org.eolang.jeo.takes</tail>\n"
                + "         <part>org.eolang.jeo.takes</part>\n"
                + "      </meta>\n"
                + "   </metas>\n"
                + "   <objects>\n"
                + "      <o abstract=\"\" name=\"Application$TimeLog\">\n"
                + "         <o base=\"int\" data=\"bytes\" name=\"access\">00 00 00 00 00 00 00 20</o>\n"
                + "         <o base=\"string\" data=\"bytes\" name=\"supername\">6A 61 76 61 2F 6C 61 6E 67 2F 4F 62 6A 65 63 74</o>\n"
                + "         <o abstract=\"\" name=\"route\">\n"
                + "            <o base=\"int\" data=\"bytes\" name=\"access\">00 00 00 00 00 00 00 01</o>\n"
                + "            <o base=\"string\" data=\"bytes\" name=\"descriptor\">28 4C 6F 72 67 2F 74 61 6B 65 73 2F 52 65 71 75 65 73 74 3B 29 4C 6F 72 67 2F 74 61 6B 65 73 2F 6D 69 73 63 2F 4F 70 74 3B</o>\n"
                + "            <o base=\"string\" data=\"bytes\" name=\"signature\">28 4C 6F 72 67 2F 74 61 6B 65 73 2F 52 65 71 75 65 73 74 3B 29 4C 6F 72 67 2F 74 61 6B 65 73 2F 6D 69 73 63 2F 4F 70 74 3C 4C 6F 72 67 2F 74 61 6B 65 73 2F 52 65 73 70 6F 6E 73 65 3B 3E 3B</o>\n"
                + "<o base=\"seq\" name=\"@\">\n"
                + "                <o base=\"opcode\" name=\"RETURN\">\n"
                + "                  <o base=\"int\" data=\"bytes\">00 00 00 00 00 00 00 B1</o>\n"
                + "               </o>"
                + "            </o>\n"
                + "         </o>\n"
                + "      </o>\n"
                + "   </objects>\n"
                + "</program>").bytecode(),
            Matchers.notNullValue()
        );
    }

}