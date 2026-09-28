/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link XmlParamAnnotations}.
 *
 * @since 0.15.0
 */
final class XmlParamAnnotationsTest {

    @Test
    void takesAGroupThatCarriesAnIndex() {
        MatcherAssert.assertThat(
            "a group named the way the writer names it must be taken",
            new XmlParamAnnotations(
                new XmlJeoObject(
                    new NativeXmlNode("<o base='Q.jeo.seq.of0' name='param-annotations-2'/>")
                )
            ).isParamAnnotations(),
            Matchers.is(true)
        );
    }

    @Test
    void leavesAGroupWithNoIndexAlone() {
        MatcherAssert.assertThat(
            "a name the index parser cannot read must not be taken for a group",
            new XmlParamAnnotations(
                new XmlJeoObject(
                    new NativeXmlNode("<o base='Q.jeo.seq.of0' name='param-annotations'/>")
                )
            ).isParamAnnotations(),
            Matchers.is(false)
        );
    }
}
