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
package it;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.cactoos.Output;
import org.cactoos.io.ResourceOf;
import org.eolang.parser.Syntax;

/**
 * EO source.
 *
 * @since 0.1
 */
@SuppressWarnings("JTCOP.RuleCorrectTestName")
final class EoSource {

    /**
     * Resource of EO program.
     */
    private final String resource;

    /**
     * Constructor.
     * @param resource Resource of EO program.
     */
    EoSource(final String resource) {
        this.resource = resource;
    }

    /**
     * Parse the EO program into XMIR.
     * @return XMIR.
     */
    XML parse() {
        final ResourceOf eolang = new ResourceOf(this.resource);
        final XmlOutput output = new XmlOutput();
        new Syntax("scenario", eolang, output);
        return output.xml();
    }

    /**
     * XML output.
     *
     * @since 0.1.0
     */
    private static final class XmlOutput implements Output {

        /**
         * Output stream.
         */
        private final ByteArrayOutputStream baos;

        /**
         * Constructor.
         */
        private XmlOutput() {
            this(new ByteArrayOutputStream());
        }

        /**
         * Constructor.
         * @param baos Output stream.
         */
        private XmlOutput(final ByteArrayOutputStream baos) {
            this.baos = baos;
        }

        @Override
        public OutputStream stream() {
            return this.baos;
        }

        /**
         * Convert to XML.
         * @return XML.
         */
        XML xml() {
            return new XMLDocument(this.baos.toByteArray());
        }
    }
}
