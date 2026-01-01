/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Collections;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeAttributes;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeClassProperties;
import org.eolang.jeo.representation.bytecode.BytecodeUnknownAttribute;
import org.eolang.jeo.representation.directives.DirectivesClass;
import org.eolang.jeo.representation.directives.DirectivesClassProperties;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link XmlClass}.
 * This class verifies the functionality of XML class representation,
 * ensuring proper creation and manipulation of class objects from XML.
 *
 * @since 0.1.0
 */
final class XmlClassTest {

    @Test
    void createsByName() {
        final String expected = "FooClass";
        final XmlClass klass = new XmlClass(expected);
        MatcherAssert.assertThat(
            String.format(
                "%s should create a class with name %s%n",
                klass,
                expected
            ),
            klass.bytecode(),
            Matchers.equalTo(new BytecodeClass(expected))
        );
    }

    @Test
    void createsBytecode() throws ImpossibleModificationException {
        final String name = "Foo";
        final int access = 100;
        final String signature = "java/lang/Object";
        final String supername = "java/lang/Runnable";
        final String interfce = "java/lang/Cloneable";
        final String pckg = "org.eolang.foo";
        MatcherAssert.assertThat(
            "We expect that class created from directives is equal to expected",
            new XmlClass(
                pckg,
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesClass(
                            name,
                            signature,
                            new DirectivesClassProperties(
                                access,
                                supername,
                                interfce
                            )
                        )
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeClass(
                    String.format("%s.%s", pckg, name),
                    new BytecodeClassProperties(access, signature, supername, interfce)
                )
            )
        );
    }

    @Test
    void parsesUnknownAttributes() throws ImpossibleModificationException {
        final BytecodeClass origin = new BytecodeClass(
            new ClassName("Foo"),
            Collections.emptyList(),
            Collections.emptyList(),
            new BytecodeAnnotations(),
            new BytecodeAttributes(
                new BytecodeUnknownAttribute("Some-Type", new byte[]{0, 1, 2, 3, 4, 5})
            ),
            new BytecodeClassProperties(
                100,
                "java/lang/Object",
                "java/lang/Runnable",
                "java/lang/Cloneable"
            )
        );
        MatcherAssert.assertThat(
            "We expect to receive the same bytecode class with unknown attribute",
            new XmlClass(
                "",
                new JcabiXmlNode(new Xembler(origin.directives(new Format())).xml())
            ).bytecode(),
            Matchers.equalTo(origin)
        );
    }
}
