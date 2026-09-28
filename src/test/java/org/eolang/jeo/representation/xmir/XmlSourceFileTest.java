/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlSourceFile}.
 *
 * @since 0.15.0
 */
final class XmlSourceFileTest {

    @Test
    void complainsAboutTheMissingDebugExtension() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "The message names the source file instead of the debug extension",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new XmlSourceFile(
                    new XmlJeoObject(
                        new JcabiXmlNode(
                            new XMLDocument(
                                new Xembler(
                                    new Directives().xpath("/o/o[last()]").remove()
                                ).apply(
                                    new XMLDocument(
                                        new Xembler(
                                            new BytecodeAttribute.SourceFile(
                                                "Main.java", "debug"
                                            ).directives(0, new Format())
                                        ).xml()
                                    ).inner()
                                )
                            ).toString()
                        )
                    )
                ).attribute(),
                "An attribute without a debug extension should be refused"
            ).getMessage(),
            Matchers.containsString("debug extension")
        );
    }
}
