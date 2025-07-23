/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.directives.DirectivesMetas;
import org.eolang.jeo.representation.directives.DirectivesObject;
import org.eolang.jeo.representation.directives.HasClass;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link BytecodeObject}.
 * @since 0.6
 * @todo #1130:30min Find a way to test {@link BytecodeObject#directives(String)}.
 *  When you find a way to test it, remove the @Disabled annotation from
 *  {@link #convertsSimpleClassWithMethodToXmir()} test.
 *  Most probably it will require to remove such classes as {@link HasClass}.
 */
final class BytecodeObjectTest {

    @Test
    void convertsSimpleClassWithoutConstructorToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't parse simple class without constructor",
            new Xembler(new BytecodeObject(new BytecodeClass()).directives("")).xml(),
            new HasClass("Simple")
        );
    }

    @Test
    void convertsSimpleClassWithMethodToXmir() {
        final String clazz = "WithMethod";
        final BytecodeClass bclass = new BytecodeClass(clazz).helloWorldMethod();
        final String opath = "//object";
        MatcherAssert.assertThat(
            "Can't parse simple class with method",
            new Nameless(
                new Directives(new BytecodeObject(bclass).directives(""))
                    .xpath(opath).attr("time", "0")
            ),
            Matchers.equalTo(
                new Nameless(
                    new Directives(
                        new DirectivesObject(
                            "",
                            bclass.directives(),
                            new DirectivesMetas(new ClassName(clazz))
                        )
                    ).xpath(opath).attr("time", "0")
                )
            )
        );
    }

    @Test
    void convertsClassWithPackageToXmir() throws ImpossibleModificationException {
        final String name = "ClassInPackage";
        final DirectivesObject directives = new BytecodeObject(
            "some/package",
            new BytecodeClass(String.format("some/package/%s", name)).helloWorldMethod()
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
