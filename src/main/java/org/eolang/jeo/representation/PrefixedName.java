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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
     * Blank name pattern.
     */
    private static final Pattern BLANKED = Pattern.compile("^\\s*$");

    /**
     * Delimiter lookbehind pattern.
     */
    private static final Pattern DELIMITED = Pattern.compile("(?<=^|[./])");

    /**
     * Prefixed name pattern.
     */
    private static final Pattern PREFIXED = Pattern.compile(
        String.format("(?<=^|[./])%s", Matcher.quoteReplacement(PrefixedName.PREFIX))
    );

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
        if (PrefixedName.BLANKED.matcher(this.origin).matches()) {
            throw new IllegalArgumentException(PrefixedName.BLANK);
        }
        return PrefixedName.DELIMITED
            .matcher(this.origin)
            .replaceAll(Matcher.quoteReplacement(PrefixedName.PREFIX));
    }

    /**
     * Decode name.
     * @return Decoded name.
     */
    public String decode() {
        if (PrefixedName.BLANKED.matcher(this.origin).matches()) {
            throw new IllegalArgumentException(PrefixedName.BLANK);
        }
        return PrefixedName.PREFIXED.matcher(this.origin).replaceAll("");
    }
}
