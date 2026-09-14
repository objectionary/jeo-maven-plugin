/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
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
     * Prefix to be used for encoding and decoding.
     */
    private final String prefix;

    /**
     * Original name.
     * Might be with a prefix or without it, depending on the context.
     */
    private final String origin;

    /**
     * Pattern to find positions for prefixing and existing prefixes for removal.
     */
    private final Pattern delimited;

    /**
     * Pattern to find existing prefixes for removal.
     */
    private final Pattern prefixed;

    /**
     * Constructor.
     * @param origin The original name.
     */
    public PrefixedName(final String origin) {
        this(PrefixedName.PREFIX, origin, PrefixedName.DELIMITED, PrefixedName.PREFIXED);
    }

    /**
     * Constructor.
     * @param prefix Prefix to be used for encoding and decoding
     * @param origin The original name.
     */
    public PrefixedName(final String prefix, final String origin) {
        this(
            prefix,
            origin,
            PrefixedName.DELIMITED,
            Pattern.compile(String.format("(?<=^|[./])%s", Pattern.quote(prefix)))
        );
    }

    /**
     * Constructor.
     * @param prefix Prefix to be used for encoding and decoding
     * @param origin The original name
     * @param delimited Pattern to find positions for prefixing
     * @param prefixed Pattern to find existing prefixes for removal
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public PrefixedName(
        final String prefix,
        final String origin,
        final Pattern delimited,
        final Pattern prefixed
    ) {
        this.prefix = prefix;
        this.origin = origin;
        this.delimited = delimited;
        this.prefixed = prefixed;
    }

    /**
     * Encode name.
     * @return Encoded name.
     */
    public String encode() {
        if (PrefixedName.BLANKED.matcher(this.origin).matches()) {
            throw new IllegalArgumentException(PrefixedName.BLANK);
        }
        final String prefixed = this.delimited
            .matcher(this.origin)
            .replaceAll(Matcher.quoteReplacement(this.prefix));
        return this.parts(prefixed, true);
    }

    /**
     * Decode name.
     * @return Decoded name.
     */
    public String decode() {
        if (PrefixedName.BLANKED.matcher(this.origin).matches()) {
            throw new IllegalArgumentException(PrefixedName.BLANK);
        }
        return this.parts(this.prefixed.matcher(this.origin).replaceAll(""), false);
    }

    /**
     * Encode or decode name parts while preserving the path delimiters.
     * @param value Name to transform
     * @param encode Whether to encode (or decode)
     * @return Transformed name
     */
    private String parts(final String value, final boolean encode) {
        final StringBuilder result = new StringBuilder();
        for (final String part : value.split("(?<=[./])|(?=[./])", -1)) {
            if (part.equals(".") || part.equals("/")) {
                result.append(part);
            } else if (encode && part.startsWith(this.prefix)) {
                result.append(this.prefix).append(
                    this.safe(part.substring(this.prefix.length()))
                );
            } else if (encode) {
                result.append(this.safe(part));
            } else {
                result.append(new EncodedString(part).decode());
            }
        }
        return result.toString();
    }

    /**
     * Encode a name part without changing the prefix alphabet.
     * @param part Name part
     * @return Encoded part
     */
    private String safe(final String part) {
        return new DecodedString(part).encode().replace("+", "%20").replace("%24", "$");
    }
}
