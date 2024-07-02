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
package org.eolang.jeo.representation;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link MethodName}.
 * @since 0.5
 */
final class MethodNameTest {

    @Test
    void createsMethodNameUsingMainConstructor() {
        final String name = "foo";
        MatcherAssert.assertThat(
            "Method name shouldn't be changed after creation",
            new MethodName(name, "()I").name(),
            Matchers.equalTo(name)
        );
    }

    @Test
    void createsConstructorMethodName() {
        MatcherAssert.assertThat(
            "Constructor name should be changed after creation from '<init>' to 'new'",
            new MethodName("<init>", "()I").name(),
            Matchers.equalTo("new")
        );
    }

    @Test
    void encodesMethodNameAndDescriptor() {
        MatcherAssert.assertThat(
            "Encoded method name and descriptor should be correct",
            new MethodName("bar", "(Ljava/lang/String;)V").encoded(),
            Matchers.equalTo("bar-KExqYXZhL2xhbmcvU3RyaW5nOylW")
        );
    }

    @Test
    void decodesMethodName() {
        MatcherAssert.assertThat(
            "Decoded method name should be correct",
            new MethodName("foo-KCgpSUk=").name(),
            Matchers.equalTo("foo")
        );
    }

}