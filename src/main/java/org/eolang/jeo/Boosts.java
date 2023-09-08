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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Boosts.
 * A list of boosts to apply.
 *
 * @since 0.1.0
 */
final class Boosts implements Boost {

    /**
     * All boosts.
     */
    private final Collection<? extends Boost> all;

    /**
     * Constructor.
     * @param arr Array of boosts.
     */
    Boosts(final Boost... arr) {
        this(Arrays.asList(arr));
    }

    /**
     * Constructor.
     * @param boosts Collection of boosts.
     */
    private Boosts(final Collection<? extends Boost> boosts) {
        this.all = boosts;
    }

    @Override
    public Collection<Representation> apply(final Collection<Representation> representations) {
        final Collection<Representation> result;
        if (this.all.isEmpty()) {
            result = Collections.emptyList();
        } else {
            Collection<Representation> res = representations;
            for (final Boost current : this.all) {
                res = current.apply(res);
            }
            result = res;
        }
        return result;
    }
}
