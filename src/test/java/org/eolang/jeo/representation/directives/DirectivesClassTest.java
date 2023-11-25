package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.ClassName;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesClass}.
 */
class DirectivesClassTest {

    @Test
    void createsWithSimpleConstructor() {
        MatcherAssert.assertThat(
            new XMLDocument(new Xembler(
                new DirectivesClass(new ClassName("Neo"), new DirectivesClassProperties()),
                new Transformers.Node()
            ).xmlQuietly()),
            Matchers.equalTo(
                new XMLDocument(
                    String.join(
                        "",
                        "<o abstract='' name='Neo'>",
                        "<o base='int' data='bytes' name='access'>00 00 00 00 00 00 00 00</o>",
                        "<o base='string' data='bytes' name='signature'/>",
                        "<o base='string' data='bytes' name='supername'/>",
                        "<o base='tuple' data='tuple' name='interfaces'/>",
                        "</o>"
                    )
                )
            )
        );
    }

    @Test
    void appendsField() {
        final String xml = new Xembler(
            new DirectivesClass(new ClassName("Neo")).field(new DirectivesField()),
            new Transformers.Node()
        ).xmlQuietly();
        MatcherAssert.assertThat(
            String.format("Can't append field, result is: '%s'", new XMLDocument(xml)),
            xml,
            XhtmlMatchers.hasXPath("/o[@name='Neo']/o[@base='field']")
        );
    }

    @Test
    void appendsMethod() {
        final String xml = new Xembler(
            new DirectivesClass(new ClassName("Neo")).method(new DirectivesMethod("method")),
            new Transformers.Node()
        ).xmlQuietly();
        MatcherAssert.assertThat(
            String.format("Can't append method, result is: '%s'", new XMLDocument(xml)),
            xml,
            XhtmlMatchers.hasXPath("/o[@name='Neo']/o[@name='method']")
        );
    }

}