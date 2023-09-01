/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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
package org.eolang.jeo;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.nio.file.Path;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link XmirFootprint}.
 *
 * @since 0.1.0
 * @todo #36:90min Replace anonymous class with a Fake class.
 *  We need to replace anonymous class in savesXml test method with a Fake class.
 *  For example, we can create a Fake class that implements IR interface and
 *  returns a fake XMLDocument in toEO method and an empty byte array in toBytecode method.
 *  Then we can use this Fake class in the test method and in other tests too.
 */
final class XmirFootprintTest {

    @Test
    void savesXml(@TempDir final Path temp) {
        final XmirFootprint footprint = new XmirFootprint(temp);
        footprint.apply(
            Collections.singleton(
                new IR() {
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
