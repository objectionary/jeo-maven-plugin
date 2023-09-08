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
import java.util.Collection;
import java.util.Collections;

/**
 * Logged optimization.
 * @since 0.1.0
 * @todo #13:30min Add unit tests for BoostLogged class.
 *  The unit tests should cover the next cases:
 *  - The BoostLogged class should log all IRs passed to it.
 *  - The BoostLogged class should return the same IRs as it gets.
 *  When the unit tests are ready, remove that puzzle.
 */
final class BoostLogged implements Boost {
    @Override
    public Collection<Representation> apply(final Collection<Representation> representations) {
        representations.forEach(
            ir -> Logger.info(this, "Optimization candidate: %s", ir)
        );
        return Collections.unmodifiableCollection(representations);
    }
}
