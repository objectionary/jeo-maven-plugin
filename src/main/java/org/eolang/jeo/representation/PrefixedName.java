/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.ToString;

/**
 * Java name handler for avoiding naming conflicts with EO-reserved words.
 *
 * <p>This class is used to represent any Java class or method name and provides
 * encoding/decoding functionality to avoid naming conflicts with EO-reserved words.
 * Names are prefixed with "j$" to distinguish them from EO keywords.</p>
 *
 * <p>You can read more about the problem
 * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/276">here</a></p>
 * @since 0.1.0
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
     * @param origin The original name (may or may not be prefixed)
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
