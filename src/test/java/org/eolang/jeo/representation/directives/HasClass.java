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
 * Matcher to check if the received XMIR document has a class with a given name.
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
     * @param name Class name.
     */
    public HasClass(final String name) {
        this.name = new PrefixedName(name).encode();
        this.additional = new ArrayList<>(0);
    }

    /**
     * Add additional check for package.
     * Package example:
     * <p>
     * {@code
     * &lt;program&gt;
     *   &lt;metas&gt;
     *     &lt;meta line="1"&gt;
     *        &lt;head&gt;package&lt;/head&gt;
     *        &lt;tail&gt;a.b.c&lt;/tail&gt;
     *        &lt;part&gt;a.b.c&lt;/part&gt;
     *     &lt;/meta&gt;
     *   &lt;/metas&gt;
     * &lt;/program&gt;
     * }
     * </p>
     * @param pckg Package name.
     * @return This matcher.
     */
    public HasClass inside(final String pckg) {
        this.additional.add("/program/metas/meta/head[text()='package']/text()");
        this.additional.add(
            String.format(
                "/program/metas/meta/tail[text()='%s']/text()",
                new PrefixedName(pckg).encode()
            )
        );
        this.additional.add(
            String.format(
                "/program/metas/meta/part[text()='%s']/text()",
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
     * All checks.
     * @return List of XPath expressions.
     */
    private List<String> checks() {
        return Stream.concat(
            Stream.of(String.format("/program/objects/o[@name='%s']/text()", this.name)),
            this.additional.stream()
        ).collect(Collectors.toList());
    }
}
