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

import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link XmlMethod}.
 * @since 0.1
 */
class XmlMethodTest {

    @Test
    public void retrievesSimpleInvokeVirtualCalls() {
        MatcherAssert.assertThat(
            "Excatly one invoke virtual call is expected",
            new XmlMethod(
                "<o>",
                "<o base='opcode' name='GETFIELD-180-31'>",
                "  <o base='string' data='bytes'>6F 72 67 2F 65 6F 6C 61 6E 67 2F 6A 65 6F 2F 42</o>\n",
                "  <o base='string' data='bytes'>61</o>\n",
                "  <o base='string' data='bytes'>4C 6F 72 67 2F 65 6F 6C 61 6E 67 2F 6A 65 6F 2F 41 3B</o>\n",
                "</o>\n",
                "<o base='opcode' name='INVOKEVIRTUAL-182-32'>\n",
                "  <o base='string' data='bytes'>6F 72 67 2F 65 6F 6C 61 6E 67 2F 6A 65 6F 2F 41</o>\n",
                "  <o base='string' data='bytes'>66 6F 6F</o>\n",
                "  <o base='string' data='bytes'>28 29 49</o>\n",
                "</o></o>"
            ).invokeVirtuals(),
            Matchers.hasSize(1)
        );
    }

    @Test
    void retrievesInvokeVirtualCallsWithArguments() {
        final List<XmlInvokeVirtual> all = new XmlMethod(
            "<o>",
            "<o base='opcode' name='GETFIELD-180-31'>",
            "  <o base='string' data='bytes'>6F 72 67 2F 65 6F 6C 61 6E 67 2F 6A 65 6F 2F 42</o>\n",
            "  <o base='string' data='bytes'>61</o>\n",
            "  <o base='string' data='bytes'>4C 6F 72 67 2F 65 6F 6C 61 6E 67 2F 6A 65 6F 2F 41 3B</o>\n",
            "</o>\n",
            "<o base='opcode' name='ICONST_1-4-32'",
            "<o base='opcode' name='INVOKEVIRTUAL-182-33'>\n",
            "  <o base='string' data='bytes'>6F 72 67 2F 65 6F 6C 61 6E 67 2F 6A 65 6F 2F 41</o>\n",
            "  <o base='string' data='bytes'>66 6F 6F</o>\n",
            "  <o base='string' data='bytes'>28 29 49</o>\n",
            "</o></o>"
        ).invokeVirtuals();
        final XmlInvokeVirtual call = all.get(0);
        MatcherAssert.assertThat(
            call.arguments(),
            Matchers.hasSize(1)
        );
        MatcherAssert.assertThat(
            "Field name should be 'a' in hex",
            call.fieldName(),
            Matchers.equalTo("61")
        );
        MatcherAssert.assertThat(
            "Field type should be 'Lorg/eolang/jeo/A;' in hex",
            call.fieldType(),
            Matchers.equalTo("4C 6F 72 67 2F 65 6F 6C 61 6E 67 2F 6A 65 6F 2F 41 3B")
        );
        MatcherAssert.assertThat(
            "Method name should be 'foo' in hex",
            call.methodName(),
            Matchers.equalTo("66 6F 6F")
        );
    }

    @Test
    void retrievesEmptyListOfInvokeVirtualCalls() {
        MatcherAssert.assertThat(
            "No invoke virtual calls are expected",
            new XmlMethod(
                "<o>",
                "<o base='opcode' name='GETFIELD-180-31'>",
                "  <o base='string' data='bytes'>6F 72 67 2F 65 6F 6C 61 6E 67 2F 6A 65 6F 2F 42</o>\n",
                "  <o base='string' data='bytes'>61</o>\n",
                "  <o base='string' data='bytes'>4C 6F 72 67 2F 65 6F 6C 61 6E 67 2F 6A 65 6F 2F 41 3B</o>\n",
                "</o>\n",
                "</o></o>"
            ).invokeVirtuals(),
            Matchers.empty()
        );
    }
}
