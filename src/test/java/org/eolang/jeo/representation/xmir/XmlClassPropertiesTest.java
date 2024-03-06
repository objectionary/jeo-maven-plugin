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

import java.util.Arrays;
import java.util.Optional;
import org.eolang.jeo.representation.directives.DirectivesClassProperties;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link org.eolang.jeo.representation.xmir.XmlClassProperties}.
 * @since 0.1
 */
final class XmlClassPropertiesTest {

    @Test
    void retrievesAccessModifier() {
        final int expected = Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | Opcodes.ACC_SUPER;
        final int actual = new XmlClass(
            "Language",
            new DirectivesClassProperties(expected)
        ).properties().access();
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

    @Test
    void retrievesSignature() {
        final String expected = "Ljava/util/List<Ljava/lang/String;>;";
        final Optional<String> actual = new XmlClass(
            new DirectivesClassProperties(0, expected)
        ).properties().signature();
        MatcherAssert.assertThat(
            String.format("Signature is not present, expected %s", expected),
            actual.isPresent(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            String.format(
                "Can't retrieve signature correctly, expected %s, got %s",
                expected,
                actual.get()
            ),
            actual.get(),
            Matchers.is(expected)
        );
    }

    @Test
    void retrievesSupernameIfDefined() {
        final String expected = "some/custom/Supername";
        final String supername = new XmlClass(new DirectivesClassProperties(0, "", expected))
            .properties()
            .supername();
        MatcherAssert.assertThat(
            String.format(
                "Can't retrieve supername correctly, expected %s, got %s",
                expected,
                supername
            ),
            supername,
            Matchers.is(expected)
        );
    }

    @Test
    void retrievesSupernameIfItIsNotDefinedExplicitly() {
        final String expected = "java/lang/Object";
        final String supername = new XmlClass("DefaultClass")
            .properties()
            .supername();
        MatcherAssert.assertThat(
            String.format(
                "Can't retrieve default supername correctly, expected %s, got %s",
                expected,
                supername
            ),
            supername,
            Matchers.is(expected)
        );
    }

    @Test
    void retrievesInterfacesIfTheyAreDefined() {
        final String[] expected = {"java/util/List", "java/util/Collection"};
        final String[] interfaces = new XmlClass(new DirectivesClassProperties(0, "", "", expected))
            .properties()
            .interfaces();
        MatcherAssert.assertThat(
            String.format(
                "Can't retrieve interfaces correctly, expected %s, got %s",
                Arrays.toString(expected),
                Arrays.toString(interfaces)
            ),
            interfaces,
            Matchers.is(expected)
        );
    }

    @Test
    void retrievesInterfacesIfTheyAreNotDefinedExplicitly() {
        final String[] interfaces = new XmlClass("WithoutIntefaces")
            .properties()
            .interfaces();
        MatcherAssert.assertThat(
            String.format(
                "Can't retrieve default interfaces correctly, expected empty array, got %s",
                Arrays.toString(interfaces)
            ),
            interfaces,
            Matchers.emptyArray()
        );
    }
}
