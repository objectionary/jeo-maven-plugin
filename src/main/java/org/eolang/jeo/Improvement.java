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
 * Optimization improvement.
 * @since 0.1.0
 */
public interface Improvement {

    /**
     * Apply the improvement.
     * @param representations IRs to optimize.
     * @return Optimized IRs.
     */
    Collection<Representation> apply(Collection<Representation> representations);

    /**
     * Mock improvement.
     *
     * @since 0.1.0
     */
    final class Mock implements Improvement {

        /**
         * All IRs that were applied.
         */
        private final Queue<Representation> all;

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
        Mock(final Queue<Representation> all) {
            this.all = all;
        }

        @Override
        public Collection<Representation> apply(final Collection<Representation> representations) {
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

    /**
     * Dummy improvement.
     * Returns the same IRs.
     *
     * @since 0.1.0
     */
    class Dummy implements Improvement {
        @Override
        public Collection<Representation> apply(final Collection<Representation> representations) {
            return Collections.unmodifiableCollection(representations);
        }
    }
}
