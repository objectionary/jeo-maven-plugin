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

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * This class tracks the time it takes to convert a bytecode to a XMIR program.
 * @since 0.6
 */
final class MeasuredEo {

    /**
     * Original transformation.
     */
    private final VerifiedEo xmir;

    /**
     * Constructor.
     * @param xmir Original transformation
     */
    MeasuredEo(final VerifiedEo xmir) {
        this.xmir = xmir;
    }

    /**
     * XML representation of the EO.
     * @return XML representation
     * @throws ImpossibleModificationException If something goes wrong
     */
    XML asXml() throws ImpossibleModificationException {
        final long start = System.currentTimeMillis();
        final XML xml = this.xmir.asXml();
        final long end = System.currentTimeMillis();
        return new XMLDocument(
            new Xembler(
                new Directives()
                    .xpath("/program[@ms]/@ms")
                    .set(String.format("%d", end - start))
            ).apply(xml.node())
        );
    }
}
