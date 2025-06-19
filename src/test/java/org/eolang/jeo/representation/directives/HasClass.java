/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.xml.XMLDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.PrefixedName;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Hamcrest matcher to verify that an XMIR document contains a class with a specific name.
 * This matcher supports additional checks for package verification and provides
 * detailed error descriptions when matches fail.
 *
 * @since 0.1.0
 */
@SuppressWarnings({
    "JTCOP.RuleAllTestsHaveProductionClass",
    "JTCOP.RuleCorrectTestName",
    "JTCOP.RuleInheritanceInTests"
})
public final class HasClass extends TypeSafeMatcher<String> {

    /**
     * Class name.
     */
    private final String name;

    /**
     * Additional checks.
     * List of xpaths that should be checked.
     */
    private final List<String> additional;

    /**
     * Constructor.
     *
     * @param name Class name to match in the XMIR document
     */
    public HasClass(final String name) {
        this.name = new PrefixedName(name).encode();
        this.additional = new ArrayList<>(0);
    }

    /**
     * Add additional check for package.
     *
     * @param pckg Package name to verify
     * @return This matcher for method chaining
     */
    public HasClass inside(final String pckg) {
        this.additional.add("/object/metas/meta/head[text()='package']/text()");
        this.additional.add(
            String.format(
                "/object/metas/meta/tail[text()='%s']/text()",
                new PrefixedName(pckg).encode()
            )
        );
        this.additional.add(
            String.format(
                "/object/metas/meta/part[text()='%s']/text()",
                new PrefixedName(pckg).encode()
            )
        );
        return this;
    }

    @Override
    public boolean matchesSafely(final String item) {
        final XMLDocument document = new XMLDocument(item);
        return this.checks().stream().map(document::xpath).noneMatch(List::isEmpty);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("XMIR doesn't satisfy the following XPath expressions:\n")
            .appendValueList("", "\n", "", this.checks());
    }

    /**
     * Get all XPath expressions for validation.
     *
     * @return List of XPath expressions to check
     */
    private List<String> checks() {
        return Stream.concat(
            Stream.of(String.format("/object/o[@name='%s']/text()", this.name)),
            this.additional.stream()
        ).collect(Collectors.toList());
    }
}
