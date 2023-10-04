package org.eolang.jeo.representation.asm;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

class DirectivesClassPropertiesTest {

    @Test
    void createsDirectives() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create proper xml",
            new Xembler(
                new Directives()
                    .add("o")
                    .append(
                        new DirectivesClassProperties(
                            1,
                            "org/eolang/SomeClass",
                            "java/lang/Object",
                            "org/eolang/SomeInterface"
                        )
                    ).up()
            ).xml(),
            Matchers.equalTo(
                String.join("",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n",
                    "<o>\n",
                    "   <o base=\"int\" data=\"bytes\" name=\"access\">00 00 00 00 00 00 00 01</o>\n",
                    "   <o base=\"string\" data=\"bytes\" name=\"signature\">6F 72 67 2F 65 6F 6C 61 6E 67 2F 53 6F 6D 65 43 6C 61 73 73</o>\n",
                    "   <o base=\"string\" data=\"bytes\" name=\"supername\">6A 61 76 61 2F 6C 61 6E 67 2F 4F 62 6A 65 63 74</o>\n",
                    "</o>\n"
                )
            )
        );
    }


}