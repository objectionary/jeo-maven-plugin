/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link NativeXmlNode}.
 * @since 0.7
 */
final class NativeXmlNodeTest {

    @Test
    void retrievesTheFirstChild() {
        MatcherAssert.assertThat(
            "Can't retrieve the first child, or the first child is not the expected one",
            new NativeXmlNode("<o><o name='inner'/></o>")
                .children()
                .findFirst()
                .orElseThrow(AssertionError::new),
            Matchers.equalTo(new NativeXmlNode("<o name='inner'/>"))
        );
    }

    @Test
    void retrievesChildren() {
        final List<XmlNode> children = new NativeXmlNode(
            "<o><o name='inner1'/><o name='inner2'/></o>"
        ).children().collect(Collectors.toList());
        MatcherAssert.assertThat(
            "Size of children is not as expected",
            children,
            Matchers.hasSize(2)
        );
        MatcherAssert.assertThat(
            "Can't retrieve the children, or the children are not the expected ones",
            children,
            Matchers.contains(
                new NativeXmlNode("<o name='inner1'/>"),
                new NativeXmlNode("<o name='inner2'/>")
            )
        );
    }

    @Test
    void retrievesAttribute() {
        final Optional<String> attribute = new NativeXmlNode("<o name='some'/>").attribute("name");
        MatcherAssert.assertThat(
            "Can't retrieve the attribute",
            attribute.isPresent(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "he attribute is not the expected one",
            attribute.get(),
            Matchers.equalTo("some")
        );
    }

    @Test
    void retrievesText() {
        MatcherAssert.assertThat(
            "Can't retrieve the text, or the text is not the expected one",
            new NativeXmlNode("<o>text</o>").text(),
            Matchers.equalTo("text")
        );
    }

    @Test
    void retrievesChild() {
        final XmlNode child = new NativeXmlNode(
            "<program><o>text</o></program>"
        ).child("o");
        final String expected = "<o>text</o>";
        MatcherAssert.assertThat(
            String.format(
                "Retrieved XML: %s does not match with expected %s",
                child,
                expected
            ),
            child,
            new IsEqual<>(new NativeXmlNode(expected))
        );
    }

    @Test
    void retrievesChildObjects() {
        final List<XmlNode> objects = new NativeXmlNode(
            "<program><o>o1</o><o>o2</o></program>"
        ).children().collect(Collectors.toList());
        final List<NativeXmlNode> expected = new ListOf<>(
            new NativeXmlNode("<o>o1</o>"),
            new NativeXmlNode("<o>o2</o>")
        );
        MatcherAssert.assertThat(
            String.format(
                "Retrieved child objects: %s don't match with expected: %s",
                objects,
                expected
            ),
            objects,
            new IsEqual<>(expected)
        );
    }

    @Test
    void retrievesByXpath() {
        final List<String> atual = new NativeXmlNode(
            "<program><o>o1</o><o>o2</o></program>"
        ).xpath("//o/text()");
        final List<String> expected = new ListOf<>("o1", "o2");
        MatcherAssert.assertThat(
            String.format(
                "Retrieved xpath: %s don't match with expected: %s",
                atual,
                expected
            ),
            atual,
            Matchers.equalTo(expected)
        );
    }
}
