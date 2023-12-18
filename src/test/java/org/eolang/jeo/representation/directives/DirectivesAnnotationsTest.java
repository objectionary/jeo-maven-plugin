package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.eolang.jeo.representation.HexData;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesAnnotation}.
 * @since 0.1
 */
class DirectivesAnnotationsTest {

    @Test
    void returnsEmptyDirectviesIfNoAnnotations() {
        MatcherAssert.assertThat(
            "Must return empty directives if no annotations",
            new DirectivesAnnotations().iterator().hasNext(),
            Matchers.is(false)
        );
    }

    @Test
    void returnsSingleAnnotation() throws ImpossibleModificationException {
        final String annotation = "Ljava/lang/Override;";
        final String xml = new Xembler(
            new DirectivesAnnotations()
                .add(new DirectivesAnnotation(annotation, true))
        ).xml();
        MatcherAssert.assertThat(
            String.format(
                "Must return single annotation with correct descriptor and visibility, but was: %n%s",
                xml
            ),
            xml,
            Matchers.allOf(
                XhtmlMatchers.hasXPath(
                    "/o[@base='tuple' and @name='annotations']/o"
                ),
                XhtmlMatchers.hasXPath(
                    String.format(
                        "/o[@base='tuple' and @name='annotations']/o/o[@base='string' and @name='descriptor' and text()='%s']",
                        new HexData(annotation).value()
                    )
                ),
                XhtmlMatchers.hasXPath(
                    String.format(
                        "/o[@base='tuple' and @name='annotations']/o/o[@base='bool' and @name='visible' and text()='%s']",
                        new HexData(true).value()
                    )
                )
            )
        );
    }

}