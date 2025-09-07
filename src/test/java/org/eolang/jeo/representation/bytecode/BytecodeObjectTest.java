/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import lombok.EqualsAndHashCode;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.directives.DirectivesMetas;
import org.eolang.jeo.representation.directives.DirectivesObject;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Directive;
import org.xembly.Directives;
import org.xembly.Transformers;
import org.xembly.Xembler;

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
            new Timeless(new BytecodeObject(clazz).directives(new Format())),
            Matchers.equalTo(
                new Timeless(
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
            new Timeless(
                new BytecodeObject(bclass).directives(new Format())
            ),
            Matchers.equalTo(
                new Timeless(
                    new DirectivesObject(
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
            new Timeless(
                new BytecodeObject(
                    "some/package",
                    clazz
                ).directives(new Format())
            ),
            Matchers.equalTo(
                new Timeless(
                    new DirectivesObject(
                        clazz.directives(),
                        new DirectivesMetas(new ClassName("some.package", name))
                    )
                )
            )
        );
    }

    /**
     * Directives without time attribute for comparison.
     * @since 0.14.0
     */
    @EqualsAndHashCode
    private final class Timeless {

        /**
         * Directives to use for XMIR generation.
         */
        @EqualsAndHashCode.Exclude
        private final Iterable<Directive> directives;

        /**
         * Constructor.
         * @param directives Directives to use.
         */
        Timeless(final Iterable<Directive> directives) {
            this.directives = directives;
        }

        @EqualsAndHashCode.Include
        @Override
        public String toString() {
            return new Xembler(
                new Directives(this.directives)
                    .xpath("//object")
                    .attr("time", "0"),
                new Transformers.Node()
            ).xmlQuietly();
        }
    }
}
