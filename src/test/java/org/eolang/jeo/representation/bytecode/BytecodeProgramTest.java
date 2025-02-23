/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.directives.DirectivesProgram;
import org.eolang.jeo.representation.directives.HasClass;
import org.eolang.jeo.representation.directives.HasMethod;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link BytecodeProgram}.
 * @since 0.6
 */
final class BytecodeProgramTest {

    @Test
    void convertsSimpleClassWithoutConstructorToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't parse simple class without constructor",
            new Xembler(new BytecodeProgram(new BytecodeClass()).directives("")).xml(),
            new HasClass("Simple")
        );
    }

    @Test
    void convertsSimpleClassWithMethodToXmir() throws ImpossibleModificationException {
        final String clazz = "WithMethod";
        final String xml = new Xembler(
            new BytecodeProgram(
                new BytecodeClass(clazz)
                    .helloWorldMethod()
            ).directives("")
        ).xml();
        MatcherAssert.assertThat(
            String.format(
                "Can't parse simple class with method, result is: '%s'",
                new XMLDocument(xml)
            ),
            xml,
            new HasMethod("main").inside(clazz)
        );
    }

    @Test
    void convertsClassWithPackageToXmir() throws ImpossibleModificationException {
        final String name = "ClassInPackage";
        final DirectivesProgram directives = new BytecodeProgram(
            "some/package",
            new BytecodeClass(name).helloWorldMethod()
        ).directives("");
        final String xml = new Xembler(directives).xml();
        final String pckg = "some.package";
        MatcherAssert.assertThat(
            String.format(
                "Can't parse '%s' class that placed under package '%s', result is: %n%s%n",
                name,
                pckg,
                new XMLDocument(xml)
            ),
            xml,
            new HasClass(name).inside(pckg)
        );
    }
}
