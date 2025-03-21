/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeTryCatchBlock;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlTryCatchEntry}.
 * @since 0.1
 */
final class XmlTryCatchEntryTest {

    @Test
    void transformsToBytecode() throws ImpossibleModificationException {
        final BytecodeTryCatchBlock block = new BytecodeTryCatchBlock(
            "a",
            "b",
            "c",
            "java/lang/Exception"
        );
        MatcherAssert.assertThat(
            "Can't convert XML try-catch entry to the correct bytecode",
            new XmlTryCatchEntry(
                new NativeXmlNode(new Xembler(block.directives()).xml())
            ).bytecode(),
            Matchers.equalTo(block)
        );
    }
}
