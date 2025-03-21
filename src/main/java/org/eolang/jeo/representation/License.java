/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import org.cactoos.Scalar;
import org.cactoos.Text;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.Sticky;
import org.cactoos.text.TextOf;

/**
 * Representation of project license.
 *
 * @since 0.6.27
 */
public final class License implements Scalar<String> {

    /**
     * The name of file with license.
     */
    private final Text content;

    /**
     * Ctor with default value.
     */
    public License() {
        this("LICENSE.txt");
    }

    /**
     * Primary ctor.
     *
     * @param content The name of file with license.
     */
    public License(final String content) {
        this.content = new Sticky(new TextOf(new ResourceOf(content)));
    }

    @Override
    public String value() {
        return this.content.toString();
    }

    @Override
    public String toString() {
        return this.value();
    }
}
