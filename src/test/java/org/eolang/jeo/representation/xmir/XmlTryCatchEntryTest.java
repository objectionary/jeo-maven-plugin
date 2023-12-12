package org.eolang.jeo.representation.xmir;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class XmlTryCatchEntryTest {


    private String trycatch = "<o base=\"trycatch\">\n"
        + "                  <o base=\"label\" name=\"start\">\n"
        + "                     <o base=\"string\" data=\"bytes\">30 65 65 66 66 62 37 37 2D 34 64 32 62 2D 34 63 31 38 2D 39 32 32 39 2D 36 32 65 39 66 61 66 39 34 61 34 34</o>\n"
        + "                  </o>\n"
        + "                  <o base=\"label\" name=\"end\">\n"
        + "                     <o base=\"string\" data=\"bytes\">62 31 65 65 38 61 34 32 2D 37 63 39 63 2D 34 63 66 39 2D 61 63 63 65 2D 39 35 62 39 38 36 38 34 34 65 36 35</o>\n"
        + "                  </o>\n"
        + "                  <o base=\"label\" name=\"handler\">\n"
        + "                     <o base=\"string\" data=\"bytes\">62 31 65 65 38 61 34 32 2D 37 63 39 63 2D 34 63 66 39 2D 61 63 63 65 2D 39 35 62 39 38 36 38 34 34 65 36 35</o>\n"
        + "                  </o>\n"
        + "                  <o base=\"string\" data=\"bytes\" name=\"type\">6A 61 76 61 2F 69 6F 2F 49 4F 45 78 63 65 70 74 69 6F 6E</o>\n"
        + "               </o>";

    @Test
    void findsType() {
        MatcherAssert.assertThat(
            new XmlTryCatchEntry(new XmlNode(this.trycatch)).type().isPresent(),
            Matchers.is(true)
        );
    }

}