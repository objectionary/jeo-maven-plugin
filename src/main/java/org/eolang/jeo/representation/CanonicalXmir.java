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

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.StClasspath;
import com.yegor256.xsline.StEndless;
import com.yegor256.xsline.TrClasspath;
import com.yegor256.xsline.TrDefault;
import com.yegor256.xsline.TrFast;
import com.yegor256.xsline.TrJoined;
import com.yegor256.xsline.Xsline;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import org.cactoos.io.InputOf;
import org.eolang.parser.EoSyntax;
import org.eolang.parser.xmir.Xmir;

/**
 * Canonical XMIR.
 * The purpose of this class is to restore the original XMIR format after PHI/UNPHI transformations.
 * You can read more about it
 * <a href="https://github.com/objectionary/eo/issues/3373#issuecomment-2361337359">here</a>.
 * @since 0.6
 */
public final class CanonicalXmir {

    /**
     * Name of the XMIR.
     */
    private final String name;

    /**
     * Canonical XMIR after "phi/unphi" transformations.
     */
    private final XML canonical;

    /**
     * Constructor.
     * @param canonical Path to the xmir Significantly modified XMIR after "phi/unphi".
     */
    public CanonicalXmir(final Path canonical) throws FileNotFoundException {
        this(
            CanonicalXmir.nameWithoutExtension(canonical),
            new XMLDocument(canonical)
        );
    }

    /**
     * Constructor.
     * @param canonical Significantly modified XMIR after "phi/unphi".
     */
    CanonicalXmir(final XML canonical) {
        this("unknown", canonical);
    }

    /**
     * Constructor.
     * @param name Name of the XMIR.
     * @param canonical Significantly modified XMIR after "phi/unphi".
     */
    private CanonicalXmir(final String name, final XML canonical) {
        this.name = name;
        this.canonical = canonical;
    }

    /**
     * Convert canonical XMIR to plain XMIR.
     * @return Plain XMIR.
     */
    public XML plain() {
        try {
            return CanonicalXmir.unroll(this.parse(this.toEo()));
        } catch (final IOException exception) {
            throw new IllegalStateException(
                "Can't parse the canonical XMIR",
                exception
            );
        }
    }

    /**
     * Convert XMIR to EO.
     * @return EO.
     */
    private String toEo() {
        return new Xmir.Default(this.canonical).toEO();
    }

    /**
     * Parse XMIR.
     * @param eoprog Eo program.
     * @return Parsed XMIR.
     * @throws IOException If fails.
     */
    @SuppressWarnings("PMD.GuardLogStatement")
    private XML parse(final String eoprog) throws IOException {
        final long start = System.currentTimeMillis();
        final XML parsed = new EoSyntax(this.name, new InputOf(eoprog)).parsed();
        final long end = System.currentTimeMillis();
        Logger.info(
            this,
            "We need to parse XMIR by using EoSyntax#parsed to make unrolling. The '%s' was parsed in %[ms]s",
            this.name,
            end - start
        );
        return parsed;
    }

    /**
     * Unroll all the changes made by the "phi/unphi" transformations.
     * @param parsed Parsed XMIR.
     * @return Unrolled XMIR.
     */
    private static XML unroll(final XML parsed) {
        return new Xsline(
            new TrFast(
                new TrJoined<Shift>(
                    new TrClasspath<Shift>(
                        "/org/eolang/parser/wrap-method-calls.xsl"
                    ).back(),
                    new TrDefault<Shift>(
                        new StEndless(
                            new StClasspath(
                                "/org/eolang/parser/roll-bases.xsl"
                            )
                        )
                    ),
                    new TrClasspath<Shift>(
                        "/org/eolang/parser/add-refs.xsl",
                        "/org/eolang/parser/add-cuts.xsl"
                    ).back(),
                    new TrDefault<Shift>(
                        new StEndless(
                            new StClasspath(
                                "/org/eolang/parser/vars-float-down.xsl"
                            )
                        )
                    ),
                    new TrClasspath<Shift>(
                        "/org/eolang/parser/remove-cuts.xsl"
                    ).back()
                ),
                CanonicalXmir.class,
                5
            )
        ).pass(parsed);
    }

    /**
     * Get name without an extension.
     * @param path Path.
     * @return Name without an extension.
     */
    private static String nameWithoutExtension(final Path path) {
        final String result;
        final String name = path.getFileName().toString();
        if (name.lastIndexOf('.') == -1) {
            result = name;
        } else {
            result = name.substring(0, path.getFileName().toString().lastIndexOf('.'));
        }
        return result;
    }
}
