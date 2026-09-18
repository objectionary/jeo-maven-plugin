/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * The options of a format, as one line.
 *
 * <p>Two formats that differ in any property answer differently, so the line
 * can be kept next to a file and compared with the options of a later run.</p>
 *
 * @since 0.15.0
 */
public final class Fingerprint {

    /**
     * The format.
     */
    private final Format format;

    /**
     * Constructor.
     * @param format The format.
     */
    public Fingerprint(final Format format) {
        this.format = format;
    }

    @Override
    public String toString() {
        final Map<String, Object> props = new TreeMap<>();
        props.put(Format.COMMENTS, this.format.comments());
        props.put(Format.LISTING, this.format.listing());
        props.put(Format.MODE, this.format.mode());
        props.put(Format.MODIFIERS, this.format.modifiers());
        props.put(Format.PRETTY, this.format.pretty());
        props.put(Format.WITH_LISTING, this.format.withListing());
        return props.entrySet()
            .stream()
            .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(";"));
    }
}
