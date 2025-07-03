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
 * Test for {@link JcabiXmlNode}.
 * @since 0.7
 */
final class JcabiXmlNodeTest {

    @Test
    void retrievesTheFirstChild() {
        MatcherAssert.assertThat(
            "Can't retrieve the first child, or the first child is not the expected one",
            new JcabiXmlNode("<o><o name='inner'/></o>")
                .children()
                .findFirst()
                .orElseThrow(AssertionError::new),
            Matchers.equalTo(new JcabiXmlNode("<o name='inner'/>"))
        );
    }

    @Test
    void retrievesChildren() {
        final List<XmlNode> children = new JcabiXmlNode(
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
                new JcabiXmlNode("<o name='inner1'/>"),
                new JcabiXmlNode("<o name='inner2'/>")
            )
        );
    }

    @Test
    void retrievesAttribute() {
        final Optional<String> attribute = new JcabiXmlNode("<o name='some'/>").attribute("name");
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
            new JcabiXmlNode("<o>text</o>").text(),
            Matchers.equalTo("text")
        );
    }

    @Test
    void retrievesChild() {
        final XmlNode child = new JcabiXmlNode(
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
            Matchers.equalTo(new JcabiXmlNode(expected))
        );
    }

    @Test
    void retrievesChildObjects() {
        final List<XmlNode> objects = new JcabiXmlNode(
            "<program><o>o1</o><o>o2</o></program>"
        ).children().collect(Collectors.toList());
        final List<JcabiXmlNode> expected = new ListOf<>(
            new JcabiXmlNode("<o>o1</o>"),
            new JcabiXmlNode("<o>o2</o>")
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
        final List<String> atual = new JcabiXmlNode(
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
