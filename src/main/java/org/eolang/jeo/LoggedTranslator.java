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

import com.jcabi.log.Logger;
import java.nio.file.Path;
import java.util.Collection;

/**
 * Translation log.
 * @since 0.2
 */
public final class LoggedTranslator implements Translator {

    /**
     * Process name.
     */
    private final String process;

    /**
     * Past participle of the process.
     * Usually it is something like:
     * "disassembled", "assembled", etc.
     */
    private final String participle;

    /**
     * From where.
     */
    private final Path input;

    /**
     * To where.
     */
    private final Path output;

    /**
     * Original translator.
     */
    private final Translator original;

    /**
     * Constructor.
     * @param process Process name.
     * @param participle Past participle of the process.
     * @param input From where.
     * @param output To where.
     * @param original Original translator.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public LoggedTranslator(
        final String process,
        final String participle,
        final Path input,
        final Path output,
        final Translator original
    ) {
        this.process = process;
        this.participle = participle;
        this.input = input;
        this.output = output;
        this.original = original;
    }

    @Override
    public Collection<? extends Representation> apply(
        final Collection<? extends Representation> representations
    ) {
        Logger.info(
            this,
            "%s files from '%[file]s' to '%[file]s'",
            this.process,
            this.input,
            this.output
        );
        final long start = System.currentTimeMillis();
        final Collection<? extends Representation> res = this.original.apply(representations);
        final long total = System.currentTimeMillis() - start;
        Logger.info(
            this,
            "Total %d files were %s in %[ms]s",
            res.size(),
            this.participle,
            total
        );
        return res;
    }
}
