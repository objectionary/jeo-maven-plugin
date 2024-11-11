/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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
package org.eolang.jeo.representation;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.Collections;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeClassProperties;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;
import org.eolang.jeo.representation.xmir.XmlProgram;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link CanonicalXmir}.
 * @since 0.6
 * @todo #705:90min Verify that PHI/UNPHI doesnt break the original bytecode.
 *  We need to implement a test that will verify that the original bytecode is not broken after
 *  applying PHI/UNPHI transformations.
 *  To do so you need to enable {@link #transformsToPlainXmir()} test and add the necessary
 *  PHI/UNPHI transformations to it.
 *  Currently it's impossible to add these transformations because of this:
 *  https://github.com/objectionary/eo/issues/3383
 */
final class CanonicalXmirTest {

    @Test
    @Disabled
    void transformsToPlainXmir() throws ImpossibleModificationException {
        final BytecodeProgram initial = new BytecodeProgram(
            "org.jeo",
            new BytecodeClass(
                "App",
                Collections.singletonList(new BytecodeMethod("main")),
                new BytecodeClassProperties(1)
            )
        );
        MatcherAssert.assertThat(
            "We expect that after all the transformations we will get the same bytecode. CanonicalXmir should be able restore the original XMIR format after PHI/UNPHI transformations ",
            new XmlProgram(
                new CanonicalXmir(
                    new XMLDocument(
                        new Xembler(initial.directives("")).xml()
                    )
                ).plain()
            ).bytecode(),
            Matchers.equalTo(initial)
        );
    }

    @Test
    void unrollsSequenceOfValuesCorrectly() {
        MatcherAssert.assertThat(
            "We expect that after the unrolling we will get correct sequence of string values.",
            new CanonicalXmir(
                new XMLDocument(
                    new Xembler(
                        new Directives()
                            .add("program")
                            .add("objects")
                            .append(
                                new XMLDocument(
                                    String.join(
                                        "\n",
                                        "<o base='.seq1' name='interfaces'> ",
                                        " <o base='.eolang'> ",
                                        "  <o base='.org'> ",
                                        "   <o base='Q'/></o> ",
                                        " </o> ",
                                        " <o as='0' base='.string'> ",
                                        "  <o base='.jeo'> ",
                                        "   <o base='.eolang'> ",
                                        "    <o base='.org'> ",
                                        "     <o base='Q'/></o> ",
                                        "   </o> ",
                                        "  </o> ",
                                        "  <o as='0' base='.bytes'> ",
                                        "   <o base='.eolang'> ",
                                        "    <o base='.org'> ",
                                        "     <o base='Q'/></o> ",
                                        "   </o> ",
                                        "   <o base='org.eolang.bytes' data='bytes'>6A 61 76 61 2F 69 6F 2F 43 6C 6F 73 65 61 62 6C 65</o> ",
                                        "  </o> ",
                                        " </o> ",
                                        "</o>"
                                    )
                                ).node()
                            )
                            .up()
                            .up()
                    ).xmlQuietly()
                )
            ).plain().toString(),
            XhtmlMatchers.hasXPath(
                ".//o[@base='org.eolang.seq1']/o[@base='org.eolang.jeo.string']/o[@base='bytes']"
            )
        );
    }

    @Test
    void transfomsBigFile() throws Exception {
        final String name = "Union.xmir";

        final long start = System.currentTimeMillis();
        final XML actual = new CanonicalXmir(
            new XMLDocument(
                new ResourceOf(String.format("xmir/unphi/%s", name)).stream()
            )
        ).plain();
        final long end = System.currentTimeMillis();
        System.out.printf(
            "CanonicalXmirTest.transfomsBigFile: %s: %d ms\n",
            name,
            end - start
        );
        final XML expected = new XMLDocument(
            new ResourceOf(String.format("xmir/unroll/%s", name)).stream());
        MatcherAssert.assertThat(
            "The XMIR should be transformed correctly",
            actual,
            Matchers.equalTo(expected)
        );

    }
}
