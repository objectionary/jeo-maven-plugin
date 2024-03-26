package org.eolang.jeo.representation.xmir;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

class XmlFrameTest {

    @Test
    void parsesRawXmirNode() {
        final XmlFrame frame = new XmlFrame(
            "<?xml version='1.0' encoding='UTF-8'?>\n",
            "<o base='frame'>\n",
            " <o base='int' data='bytes' name='type'>FF FF FF FF FF FF FF FF</o>\n",
            " <o base='int' data='bytes' name='nlocal'>00 00 00 00 00 00 00 02</o>\n",
            " <o base='tuple' name='local' star=''>\n",
            "  <o base='string' data='bytes'>6A 61 76 61 2F 6C 61 6E 67 2F 4F 62 6A 65 63 74</o>\n",
            "  <o base='int' data='bytes'>00 00 00 00 00 00 00 04</o>\n",
            " </o>\n",
            " <o base='int' data='bytes' name='nstack'>00 00 00 00 00 00 00 03</o>\n",
            " <o base='tuple' name='stack' star=''>\n",
            "  <o base='string' data='bytes'>6A 61 76 61 2F 6C 61 6E 67 2F 53 74 72 69 6E 67</o>\n",
            "  <o base='int' data='bytes'>00 00 00 00 00 00 00 03</o>\n",
            " </o>\n",
            "</o>\n"
        );
        MatcherAssert.assertThat(
            "Frame type is not correct.",
            frame.type(),
            Matchers.equalTo(Opcodes.F_NEW)
        );
        MatcherAssert.assertThat(
            "Number of local variables for the expected frame is not correct.",
            frame.nlocal(),
            Matchers.equalTo(2)
        );
        MatcherAssert.assertThat(
            "Number of stack variables for the expected frame is not correct.",
            frame.nstack(),
            Matchers.equalTo(3)
        );
        MatcherAssert.assertThat(
            "Types of local variables for the expected frame are not correct.",
            frame.locals(),
            Matchers.arrayContaining("java/lang/Object", Opcodes.LONG)
        );
        MatcherAssert.assertThat(
            "Types of stack variables for the expected frame are not correct.",
            frame.stack(),
            Matchers.arrayContaining("java/lang/String", Opcodes.DOUBLE)
        );
    }

}