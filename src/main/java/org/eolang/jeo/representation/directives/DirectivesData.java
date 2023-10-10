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
package org.eolang.jeo.representation.directives;

import org.eolang.jeo.representation.HexData;
import org.xembly.Directives;

/**
 * Data Object Directive in EO language.
 *
 * @since 0.1.0
 */
final class DirectivesData {

    /**
     * Data.
     */
    private final Object data;

    /**
     * Name.
     */
    private final String name;

    /**
     * Constructor.
     * @param data Data.
     */
    DirectivesData(final Object data) {
        this("", data);
    }

    /**
     * Constructor.
     * @param name Name.
     * @param data Data.
     */
    DirectivesData(final String name, final Object data) {
        this.data = data;
        this.name = name;
    }

    /**
     * Directives.
     * @return Directives
     */
    Directives directives() {
        final HexData hex = new HexData(this.data);
        final Directives directives = new Directives().add("o")
            .attr("base", hex.type())
            .attr("data", "bytes");
        if (!this.name.isEmpty()) {
            directives.attr("name", this.name);
        }
        return directives.set(hex.value()).up();
    }
}
