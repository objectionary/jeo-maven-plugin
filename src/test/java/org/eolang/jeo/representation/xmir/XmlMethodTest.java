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
    void retrievesSimpleInvokeVirtualCalls() {
        MatcherAssert.assertThat(
            "Exactly one invoke virtual call is expected",
            new XmlMethod(
                "<o base='seq'>",
                "<o base='opcode' name='GETFIELD'>",
                "  <o base='int' data='bytes'>00 00 00 00 00 00 00 B4</o>",
                "  <o base='string' data='bytes'>6F 72 67 2F 65 6F 6C 61 6E 67 2F 6A 65 6F 2F 42</o>\n",
                "  <o base='string' data='bytes'>61</o>\n",
                "  <o base='string' data='bytes'>4C 6F 72 67 2F 65 6F 6C 61 6E 67 2F 6A 65 6F 2F 41 3B</o>\n",
                "</o>\n",
                "<o base='opcode' name='INVOKEVIRTUAL'>\n",
                "  <o base='int' data='bytes'>00 00 00 00 00 00 00 B6</o>",
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
            "<o base='seq'>",
            "<o base='opcode' name='GETFIELD'>",
            "  <o base='int' data='bytes'>00 00 00 00 00 00 00 B4</o>",
            "  <o base='string' data='bytes'>6F 72 67 2F 65 6F 6C 61 6E 67 2F 6A 65 6F 2F 42</o>",
            "  <o base='string' data='bytes'>61</o>",
            "  <o base='string' data='bytes'>4C 6F 72 67 2F 65 6F 6C 61 6E 67 2F 6A 65 6F 2F 41 3B</o>",
            "</o>",
            "<o base='opcode' name='ICONST_1'>",
            "  <o base='int' data='bytes'>00 00 00 00 00 00 00 04</o>",
            "</o>",
            "<o base='opcode' name='INVOKEVIRTUAL'>",
            "  <o base='int' data='bytes'>00 00 00 00 00 00 00 B6</o>",
            "  <o base='string' data='bytes'>6F 72 67 2F 65 6F 6C 61 6E 67 2F 6A 65 6F 2F 41</o>",
            "  <o base='string' data='bytes'>66 6F 6F</o>",
            "  <o base='string' data='bytes'>28 29 49</o>",
            "</o></o>"
        ).invokeVirtuals();
        final XmlInvokeVirtual call = all.get(0);
        MatcherAssert.assertThat(
            "Exactly one invoke argument is expected",
            call.arguments(),
            Matchers.hasSize(1)
        );
    }

    @Test
    void retrievesEmptyListOfInvokeVirtualCalls() {
        MatcherAssert.assertThat(
            "No invoke virtual calls are expected",
            new XmlMethod(
                "<o base='seq'>",
                "<o base='opcode' name='GETFIELD'>",
                "  <o base='int' data='bytes'>00 00 00 00 00 00 00 B4</o>",
                "  <o base='string' data='bytes'>6F 72 67 2F 65 6F 6C 61 6E 67 2F 6A 65 6F 2F 42</o>",
                "  <o base='string' data='bytes'>61</o>",
                "  <o base='string' data='bytes'>4C 6F 72 67 2F 65 6F 6C 61 6E 67 2F 6A 65 6F 2F 41 3B</o>",
                "</o></o>"
            ).invokeVirtuals(),
            Matchers.empty()
        );
    }

    @Test
    void createsMethodByValues() {
        final String name = "name";
        final int access = 0;
        final String descriptor = "()V";
        final XmlMethod method = new XmlMethod(name, access, descriptor);
        MatcherAssert.assertThat(
            "Method name is not equal to expected",
            method.name(),
            Matchers.equalTo(name)
        );
        MatcherAssert.assertThat(
            "Method access is not equal to expected",
            method.access(),
            Matchers.equalTo(access)
        );
        MatcherAssert.assertThat(
            "Method descriptor is not equal to expected",
            method.descriptor(),
            Matchers.equalTo(descriptor)
        );
    }
}
