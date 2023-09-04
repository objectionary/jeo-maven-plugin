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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Optimization boost.
 * @since 0.1.0
 */
public interface Boost {

    /**
     * Apply the boost.
     * @param representations IRs to optimize.
     * @return Optimized IRs.
     */
    Collection<IR> apply(Collection<IR> representations);

    /**
     * Mock boost.
     *
     * @since 0.1.0
     */
    final class Mock implements Boost {

        /**
         * All IRs that were applied.
         */
        private final Queue<IR> all;

        /**
         * Constructor.
         */
        Mock() {
            this(new LinkedList<>());
        }

        /**
         * Constructor.
         * @param all All IRs that were applied.
         */
        Mock(final Queue<IR> all) {
            this.all = all;
        }

        @Override
        public Collection<IR> apply(final Collection<IR> representations) {
            this.all.addAll(representations);
            return Collections.unmodifiableCollection(representations);
        }

        /**
         * Check if the boost was applied.
         * @return True if the boost was applied.
         */
        boolean isApplied() {
            return !this.all.isEmpty();
        }
    }
}
