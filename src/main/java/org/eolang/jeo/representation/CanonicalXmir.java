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
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.StClasspath;
import com.yegor256.xsline.StEndless;
import com.yegor256.xsline.TrClasspath;
import com.yegor256.xsline.TrDefault;
import com.yegor256.xsline.TrJoined;
import com.yegor256.xsline.Xsline;
import java.io.IOException;
import org.cactoos.io.InputOf;
import org.eolang.parser.EoSyntax;
import org.eolang.parser.xmir.Xmir;

public final class CanonicalXmir {

    private final XML canonical;

    /**
     * Constructor.
     * @param canonical Significantly modified XMIR after "phi/unphi".
     */
    public CanonicalXmir(final XML canonical) {
        this.canonical = canonical;
    }

    /**
     * Convert canonical XMIR to plain XMIR.
     * @return Plain XMIR.
     */
    public XML plain() {
        try {
            final String eo = this.toEo();
            final XML parsed = CanonicalXmir.parse(eo);
            return CanonicalXmir.unroll(parsed);
        } catch (final IOException exception) {
            throw new IllegalStateException(
                "Can't parse the canonical XMIR",
                exception
            );
        }
    }

    private static XML unroll(final XML parsed) {
        final TrJoined<Shift> shifts = new TrJoined<>(
            new TrClasspath<>(
                "/org/eolang/parser/wrap-method-calls.xsl"
            ).back(),
            new TrDefault<>(
                new StEndless(
                    new StClasspath(
                        "/org/eolang/parser/roll-bases.xsl"
                    )
                )
            ),
            new TrClasspath<>(
                "/org/eolang/parser/add-refs.xsl",
                "/org/eolang/parser/vars-float-down.xsl",
                "/org/eolang/parser/roll-data.xsl"
            ).back()
        );
        return new Xsline(shifts).pass(parsed);
    }

    private String toEo() {
        return new Xmir.Default(this.canonical).toEO();
    }

    private static XML parse(final String eoprog) throws IOException {
        return new EoSyntax("name", new InputOf(eoprog)).parsed();
    }

}
