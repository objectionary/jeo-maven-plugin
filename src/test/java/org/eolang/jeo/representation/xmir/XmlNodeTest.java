package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link XmlNode}.
 * @since 0.1
 */
class XmlNodeTest {


    @Test
    void retrieveTheFirstChild() {
        MatcherAssert.assertThat(
            "Can't retrieve the first child, or the first child is not the expected one",
            new XmlNode("<o><o name='inner'/></o>").firstChild(),
            Matchers.equalTo(new XmlNode("<o name='inner'/>"))
        );
    }

    @Test
    void retrievesChildren() {
        final List<XmlNode> children = new XmlNode("<o><o name='inner1'/><o name='inner2'/></o>")
            .children().collect(Collectors.toList());
        MatcherAssert.assertThat(
            "Size of children is not as expected",
            children,
            Matchers.hasSize(2)
        );
        MatcherAssert.assertThat(
            "Can't retrieve the children, or the children are not the expected ones",
            children,
            Matchers.contains(
                new XmlNode("<o name='inner1'/>"),
                new XmlNode("<o name='inner2'/>")
            )
        );
    }

    @Test
    void retrievesAttribute() {
        final Optional<String> attribute = new XmlNode("<o name='some'/>").attribute("name");
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
            new XmlNode("<o>text</o>").text(),
            Matchers.equalTo("text")
        );
    }
}