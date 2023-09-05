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

/**
 * Intermediate representation of a class files from XMIR.
 *
 * @since 0.1.0
 * @todo #39:90min Add unit test for XmirIR class.
 *  The test should check all the methods of the {@link org.eolang.jeo.XmirIR} class.
 *  Don't forget to test corner cases.
 *  When the test is ready, remove this puzzle.
 * @todo #39:90min Implement XmirIR#name() method.
 *  The method should return the name of the object from XMIR.
 *  We have to parse the XML and extract the name from it.
 *  You can find examples for XMIR in the EOlang repository:
 *  https://github.com/objectionary/eo
 *  When the method is ready, remove this puzzle.
 */
public final class XmirIR implements IR {

    /**
     * XML.
     */
    private final XML xml;

    /**
     * Constructor.
     */
    XmirIR() {
        this(new XMLDocument("<xmir/>"));
    }

    /**
     * Constructor.
     * @param xml XML.
     */
    private XmirIR(final XML xml) {
        this.xml = XmirIR.xmir(xml);
    }

    @Override
    public String name() {
        return "todo";
    }

    @Override
    public XML toEO() {
        return this.xml;
    }

    @Override
    public byte[] toBytecode() {
        return new byte[0];
    }


    private static XML xmir(final XML xml) {
        new Schema(xml).check();
        return xml;
    }
}
