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
package org.eolang.jeo.representation;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.directives.DirectivesClassVisitor;
import org.eolang.parser.Schema;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Class that verifies EO representation and fixes some small syntax bugs.
 *
 * @since 5.1
 */
final class VerifiedEo {

    /**
     * Directives to verify.
     */
    private final DirectivesClassVisitor directives;

    /**
     * Constructor.
     * @param dirs Directives to verify.
     */
    VerifiedEo(final DirectivesClassVisitor dirs) {
        this.directives = dirs;
    }

    /**
     * XML representation of the EO.
     * We also check if the EO is correct.
     * @return XML representation.
     * @throws ImpossibleModificationException If something goes wrong.
     */
    XML asXml() throws ImpossibleModificationException {
        final XML res = new XMLDocument(new Xembler(this.directives).xml());
        new Schema(res).check();
        return res;
    }

}
