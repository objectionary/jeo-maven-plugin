/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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
package org.eolang.jeo.representation;

import lombok.ToString;

/**
 * Java name.
 * This class is used to represent any java class or method name and to avoid
 * naming conflicts with EO reserved words.
 * You can read more about the problem
 * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/276">here</a>
 * In the first implementation of this class it just added a 'j' prefix to any name.
 *
 * @since 0.1
 */
@ToString
public final class PrefixedName {

    /**
     * Prefix.
     */
    private static final String PREFIX = "j$";

    /**
     * Blank name error message.
     */
    private static final String BLANK = "Name can't be blank";

    /**
     * Delimiter.
     */
    private static final String DELIMITER = "/";

    /**
     * Original name.
     * Might be with 'j' prefix or without it, depending on the context.
     */
    private final String origin;

    /**
     * Constructor.
     * @param origin Original name.
     */
    public PrefixedName(final String origin) {
        this.origin = origin;
    }

    /**
     * Encode name.
     * @return Encoded name.
     */
    public String encode() {
        final String res;
        if (this.origin.replace(" ", "").isEmpty()) {
            throw new IllegalArgumentException(PrefixedName.BLANK);
        } else if (this.origin.contains(PrefixedName.DELIMITER)) {
            final String[] split = this.origin.split(PrefixedName.DELIMITER);
            split[split.length - 1] = new PrefixedName(split[split.length - 1]).encode();
            res = String.join(PrefixedName.DELIMITER, split);
        } else {
            res = String.format("%s%s", PrefixedName.PREFIX, this.origin);
        }
        return res;
    }

    /**
     * Decode name.
     * @return Decoded name.
     */
    public String decode() {
        final String res;
        if (this.origin.replace(" ", "").isEmpty()) {
            throw new IllegalArgumentException(PrefixedName.BLANK);
        } else if (this.origin.startsWith(PrefixedName.PREFIX)) {
            res = this.origin.substring(PrefixedName.PREFIX.length());
        } else if (this.origin.contains(PrefixedName.DELIMITER)) {
            final String[] split = this.origin.split(PrefixedName.DELIMITER);
            split[split.length - 1] = new PrefixedName(split[split.length - 1]).decode();
            res = String.join(PrefixedName.DELIMITER, split);
        } else {
            res = this.origin;
        }
        return res;
    }
}
