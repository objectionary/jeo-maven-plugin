package org.eolang.jeo;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.nio.file.Path;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class XmirFootprintTest {

    @Test
    void savesXml(@TempDir final Path temp) {
        final XmirFootprint footprint = new XmirFootprint(temp);
        footprint.apply(
            Collections.singleton(new IR() {
                @Override
                public XML toEO() {
                    return new XMLDocument("<test/>");
                }

                @Override
                public byte[] toBytecode() {
                    return new byte[0];
                }
            })
        );
        MatcherAssert.assertThat(
            "XML file was not saved",
            temp.resolve("jeo")
                .resolve("xmir")
                .resolve("org")
                .resolve("eolang")
                .resolve("jeo")
                .resolve("Application.xmir").toFile(),
            FileMatchers.anExistingFile()
        );
    }

}