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

    private final String name;

    private final String verb;

    private final Path from;
    private final Path to;

    private final Translator original;

    public LoggedTranslator(
        final String name,
        final String verb,
        final Path from,
        final Path to,
        final Translator original
    ) {
        this.name = name;
        this.verb = verb;
        this.from = from;
        this.to = to;
        this.original = original;
    }

    @Override
    public Collection<? extends Representation> apply(
        final Collection<? extends Representation> representations
    ) {
        Logger.info(this, "%s files from '%[file]s' to '%[file]s'", this.name, this.from, this.to);
        long start = System.currentTimeMillis();
        final Collection<? extends Representation> res = this.original.apply(representations);
        long total = System.currentTimeMillis() - start;
        Logger.info(this, "Total %d files were %s in %[ms]s", res.size(), this.verb, total);
        return res;
    }
}
