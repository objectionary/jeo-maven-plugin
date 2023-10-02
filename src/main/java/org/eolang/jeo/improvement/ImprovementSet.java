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
package org.eolang.jeo.improvement;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.eolang.jeo.Improvement;
import org.eolang.jeo.Representation;

/**
 * Improvements.
 * A list of improvements to apply.
 *
 * @since 0.1.0
 */
public final class ImprovementSet implements Improvement {

    /**
     * All boosts.
     */
    private final Collection<? extends Improvement> all;

    /**
     * Constructor.
     * @param arr Array of boosts.
     */
    public ImprovementSet(final Improvement... arr) {
        this(Arrays.asList(arr));
    }

    /**
     * Constructor.
     * @param boosts Collection of boosts.
     */
    private ImprovementSet(final Collection<? extends Improvement> boosts) {
        this.all = boosts;
    }

    @Override
    public Collection<? extends Representation> apply(
        final Collection<? extends Representation> representations
    ) {
        final Collection<? extends Representation> result;
        if (this.all.isEmpty()) {
            result = Collections.emptyList();
        } else {
            Collection<? extends Representation> res = representations;
            for (final Improvement current : this.all) {
                res = current.apply(res);
            }
            result = res;
        }
        return result;
    }
}
