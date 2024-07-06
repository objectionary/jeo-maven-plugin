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
package org.eolang.jeo.representation.directives;

import java.util.stream.Stream;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.jeo.representation.xmir.XmlTryCatchEntry;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.Label;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesTryCatch}.
 * @since 0.5
 */
final class DirectivesTryCatchTest {

    @Test
    void correctlyConvertsToXmir() throws ImpossibleModificationException {
        final Label start = new Label();
        final Label end = new Label();
        final Label handler = new Label();
        final String type = "java/lang/Exception";
        final XmlTryCatchEntry entry = new XmlTryCatchEntry(
            new XmlNode(
                new Xembler(
                    new DirectivesTryCatch(start, end, handler, type)
                ).xml()
            )
        );
        MatcherAssert.assertThat(
            "We failed to convert try-catch directives to XML. Start label is incorrect.",
            entry.start(),
            Matchers.equalTo(start)
        );
        MatcherAssert.assertThat(
            "We failed to convert try-catch directives to XML. End label is incorrect.",
            entry.end(),
            Matchers.equalTo(end)
        );
        MatcherAssert.assertThat(
            "We failed to convert try-catch directives to XML. Handler label is incorrect.",
            entry.handler(),
            Matchers.equalTo(handler)
        );
        MatcherAssert.assertThat(
            "We failed to convert try-catch directives to XML. Exception type is incorrect.",
            entry.type(),
            Matchers.equalTo(type)
        );
    }

    /**
     * Checks several different cases of converting try-catch statements with different structures.
     * @param start Where the try-catch block starts.
     * @param end Where the try-catch block ends.
     * @param handler Code to handle try-catch block.
     * @param type The type of error that might occur.
     * @throws ImpossibleModificationException in case of incorrect XML.
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    @ParameterizedTest
    @MethodSource("cases")
    void correctlyConvertsToXmirIfSomeLabelsAreAbsent(
        final Label start,
        final Label end,
        final Label handler,
        final String type
    ) throws ImpossibleModificationException {
        final XmlTryCatchEntry entry = new XmlTryCatchEntry(
            new XmlNode(
                new Xembler(
                    new DirectivesTryCatch(start, end, handler, type)
                ).xml()
            )
        );
        MatcherAssert.assertThat(
            "We failed to convert try-catch directives to XML. Start label is incorrect.",
            entry.start(),
            Matchers.equalTo(start)
        );
        MatcherAssert.assertThat(
            "We failed to convert try-catch directives to XML. End label is incorrect.",
            entry.end(),
            Matchers.equalTo(end)
        );
        MatcherAssert.assertThat(
            "We failed to convert try-catch directives to XML. Handler label is incorrect.",
            entry.handler(),
            Matchers.equalTo(handler)
        );
        MatcherAssert.assertThat(
            "We failed to convert try-catch directives to XML. Exception type is incorrect.",
            entry.type(),
            Matchers.equalTo(type)
        );
    }

    /**
     * Test cases.
     * For {@link #correctlyConvertsToXmirIfSomeLabelsAreAbsent(Label, Label, Label, String)}.
     * @return Test cases.
     */
    private static Stream<Arguments> cases() {
        return Stream.of(
            Arguments.of(new Label(), new Label(), new Label(), "java/lang/Exception"),
            Arguments.of(new Label(), new Label(), new Label(), null),
            Arguments.of(new Label(), new Label(), null, null),
            Arguments.of(new Label(), null, null, null),
            Arguments.of(null, null, null, null)
        );
    }

}
