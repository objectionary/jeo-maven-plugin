/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.matchers;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher to check if the received XML document is the same as the expected one.
 * Smart comparison of XML documents that ignores 'line' attributes.
 * @since 0.6
 */
public final class SameXml extends TypeSafeMatcher<String> {

    /**
     * Expected XML document.
     */
    private final String expected;

    /**
     * Constructor.
     * @param xml Expected XML document.
     */
    public SameXml(final XML xml) {
        this(xml.toString());
    }

    /**
     * Constructor.
     * @param expected Expected XML document.
     */
    public SameXml(final String expected) {
        this.expected = expected;
    }

    @Override
    public boolean matchesSafely(final String item) {
        return new WithoutLines(new XMLDocument(this.expected)).value()
            .equals(new WithoutLines(new XMLDocument(item)).value());
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("XML documents is not the same.")
            .appendText("Expected:\n")
            .appendText(this.expected);
    }
}
