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
package org.eolang.jeo.representation.xmir;

import java.util.UUID;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Label;

/**
 * Test case for {@link AllLabels}.
 * @since 0.1
 */
class AllLabelsTest {

    @Test
    void retrievesLabelIfItAbsent() {
        final String id = UUID.randomUUID().toString();
        MatcherAssert.assertThat(
            String.format("Label with id '%s' was not retrieved", id),
            new AllLabels().label(id),
            Matchers.notNullValue()
        );
    }

    @Test
    void retrievesTheSameLabelIfItExists() {
        final String id = UUID.randomUUID().toString();
        final AllLabels all = new AllLabels();
        final Label first = all.label(id);
        final Label second = all.label(id);
        MatcherAssert.assertThat(
            String.format(
                "We should retrieve the same label if it exists, but labels '%s' and '%s' are different",
                first,
                second
            ),
            first,
            Matchers.sameInstance(second)
        );
    }

    @Test
    void generatesUidForNewLabel() {
        MatcherAssert.assertThat(
            "We should generate uid for new label",
            new AllLabels().uid(new Label()),
            Matchers.notNullValue()
        );
    }

    @Test
    void retrievesSameUidForSameLabel() {
        final AllLabels all = new AllLabels();
        final Label label = new Label();
        final String first = all.uid(label);
        final String second = all.uid(label);
        MatcherAssert.assertThat(
            String.format(
                "We should retrieve the same uid for the same label, but we got %s and %s",
                first,
                second
            ),
            first,
            Matchers.equalTo(second)
        );
    }
}
