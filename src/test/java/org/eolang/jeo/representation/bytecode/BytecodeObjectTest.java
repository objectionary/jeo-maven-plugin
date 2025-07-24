/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.directives.DirectivesMetas;
import org.eolang.jeo.representation.directives.DirectivesObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link BytecodeObject}.
 * @since 0.6
 */
final class BytecodeObjectTest {

    @Test
    void convertsSimpleClassWithoutConstructorToXmir() {
        final BytecodeClass clazz = new BytecodeClass();
        MatcherAssert.assertThat(
            "Can't covert simple global object with a class without constructor",
            new Nameless(new BytecodeObject(clazz).directives("")),
            Matchers.equalTo(
                new Nameless(
                    new DirectivesObject(
                        clazz.directives(),
                        new DirectivesMetas(new ClassName("Simple"))
                    )
                ))
        );
    }

    @Test
    void convertsSimpleClassWithMethodToXmir() {
        final String clazz = "WithMethod";
        final BytecodeClass bclass = new BytecodeClass(clazz).helloWorldMethod();
        MatcherAssert.assertThat(
            "Can't parse simple class with method",
            new Nameless(
                new BytecodeObject(bclass).directives("")
            ),
            Matchers.equalTo(
                new Nameless(
                    new DirectivesObject(
                        "",
                        bclass.directives(),
                        new DirectivesMetas(new ClassName(clazz))
                    )
                )
            )
        );
    }

    @Test
    void convertsClassWithPackageToXmir() {
        final String name = "ClassInPackage";
        final BytecodeClass clazz = new BytecodeClass(
            String.format("some/package/%s", name)
        ).helloWorldMethod();
        MatcherAssert.assertThat(
            "Can't convert global object to XMIR",
            new Nameless(
                new BytecodeObject(
                    "some/package",
                    clazz
                ).directives("")
            ),
            Matchers.equalTo(
                new Nameless(
                    new DirectivesObject(
                        clazz.directives(),
                        new DirectivesMetas(new ClassName("some.package", name))
                    )
                )
            )
        );
    }
}
