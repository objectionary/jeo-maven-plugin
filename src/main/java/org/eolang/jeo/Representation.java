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
import org.eolang.jeo.representation.bytecode.Bytecode;

/**
 * Intermediate representation of a class files which can be optimized.
 *
 * @since 0.1.0
 */
public interface Representation {

    /**
     * Details of representation.
     * Additional info about representation.
     * @return Representation details instance.
     */
    Details details();

    /**
     * Convert to EOlang XML representation (XMIR).
     * @return XML.
     */
    XML toEO();

    /**
     * Convert to bytecode.
     * @return Array of bytes.
     */
    Bytecode toBytecode();

    /**
     * Named representation.
     *
     * @since 0.1.0
     */
    class Named implements Representation {

        /**
         * Name of representation.
         */
        private final String name;

        /**
         * Constructor.
         * @param name Name of representation.
         */
        public Named(final String name) {
            this.name = name;
        }

        @Override
        public Details details() {
            return new Details(this.name);
        }

        @Override
        public XML toEO() {
            throw new UnsupportedOperationException(
                String.format("toEO() not implemented for %s", this.getClass().getName())
            );
        }

        @Override
        public Bytecode toBytecode() {
            throw new UnsupportedOperationException(
                String.format("toBytecode() not implemented for %s", this.getClass().getName())
            );
        }
    }

}
