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

import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Footprint of the representations as bytecode classes.
 *
 * @since 0.1.0
 */
public final class TranslationBytecodeFootprint implements Translation {

    private final SingleTranslation translation;

    /**
     * Constructor.
     * @param classes Where to save the bytecode classes.
     */
    public TranslationBytecodeFootprint(final Path classes) {
        this(new SingleTranslationLog("Assembling", "assembled", new Assemble(classes)));
    }

    public TranslationBytecodeFootprint(final SingleTranslation translation) {
        this.translation = translation;
    }

    @Override
    public Collection<? extends Representation> apply(
        final Collection<? extends Representation> representations
    ) {
        return representations.stream().map(this::assemble).collect(Collectors.toList());
    }

    /**
     * Recompile the Intermediate Representation.
     *
     * @param representation Intermediate Representation to recompile.
     */
    private Representation assemble(final Representation representation) {
        return this.translation.apply(representation);
    }
}
