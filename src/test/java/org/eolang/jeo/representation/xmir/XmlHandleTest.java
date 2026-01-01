/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.directives.DirectivesHandle;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Handle;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlHandle}.
 *
 * @since 0.3
 */
final class XmlHandleTest {

    @Test
    void convertsToHandleObject() throws ImpossibleModificationException {
        final Handle handle = new Handle(
            1, "owner", "name", "desc", false
        );
        MatcherAssert.assertThat(
            "Can't convert XML handler to the correct handle object",
            new XmlHandle(
                new NativeXmlNode(new Xembler(new DirectivesHandle(0, new Format(), handle)).xml())
            ).bytecode().asHandle(),
            Matchers.equalTo(handle)
        );
    }

}
