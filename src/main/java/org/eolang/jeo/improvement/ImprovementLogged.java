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

import com.jcabi.log.Logger;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import org.eolang.jeo.Improvement;
import org.eolang.jeo.Representation;

/**
 * Logged improvement.
 * @since 0.1.0
 */
public final class ImprovementLogged implements Improvement {

    /**
     * Logger.
     */
    private final Consumer<String> logger;

    /**
     * Constructor.
     * @since 0.1.0
     */
    public ImprovementLogged() {
        this(msg -> Logger.info(ImprovementLogged.class, msg));
    }

    /**
     * Constructor.
     * @param logger Logger.
     * @since 0.1.0
     */
    ImprovementLogged(final Consumer<String> logger) {
        this.logger = logger;
    }

    @Override
    public Collection<Representation> apply(
        final Collection<? extends Representation> representations
    ) {
        representations.forEach(this::log);
        return Collections.unmodifiableCollection(representations);
    }

    /**
     * Log the representation.
     * @param representation Representation to log.
     */
    private void log(final Representation representation) {
        this.logger.accept(
            String.format("Optimization candidate: %s", representation.details().name())
        );
    }
}
