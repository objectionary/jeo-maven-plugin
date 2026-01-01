/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesMethodParam}.
 * This class verifies the generation of method parameter directives,
 * including parameter names, indices, access modifiers, and type information.
 *
 * @since 0.9.0
 */
final class DirectivesMethodParamTest {

    /**
     * This issue was raised in #1341.
     * In come cases, the parameter name might be $this.
     * $this isn't allowed in EO as a name, so we need to append a prefix to it.
     * You can find more details here:
     * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1341">issue</a>
     */
    @Test
    void appendsPrefixForName() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the parameter name will have a prefix",
            new Xembler(
                new DirectivesMethodParam(
                    new Format(),
                    0,
                    "$this",
                    Opcodes.ACC_PUBLIC,
                    Type.VOID_TYPE
                )
            ).xml(),
            XhtmlMatchers.hasXPath(
                "./o[contains(@name, 'p$this')]"
            )
        );
    }

    @Test
    void generatesParamDirectivesWithSimpleName() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the parameter directives will be generated with a simple name that includes the name",
            new Xembler(
                new DirectivesMethodParam(
                    new Format(),
                    1,
                    "foo",
                    Opcodes.ACC_PUBLIC,
                    Type.INT_TYPE
                )
            ).xml(),
            XhtmlMatchers.hasXPath("./o[contains(@base, param) and contains(@name, 'foo')]")
        );
    }

    @Test
    void generatesParamDirectivesWithIndex() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the parameter directives will be generated with an index",
            new Xembler(
                new DirectivesMethodParam(
                    new Format(),
                    2,
                    "bar",
                    Opcodes.ACC_PRIVATE,
                    Type.DOUBLE_TYPE
                )
            ).xml(),
            XhtmlMatchers.hasXPath(
                "./o[contains(@base, param)]/o[contains(@base, 'number') and contains(@name, 'index')]"
            )
        );
    }

    @Test
    void generatesPramDirectivesWithAccessModifier() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the parameter directives will be generated with an access modifier",
            new Xembler(
                new DirectivesMethodParam(
                    new Format(),
                    3,
                    "baz",
                    Opcodes.ACC_PROTECTED,
                    Type.LONG_TYPE
                )
            ).xml(),
            XhtmlMatchers.hasXPath(
                "./o[contains(@base, param)]/o[contains(@base, 'number') and contains(@name, 'access')]"
            )
        );
    }

    @Test
    void generatesParamDirectivesWithType() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the parameter directives will be generated with a type",
            new Xembler(
                new DirectivesMethodParam(
                    new Format(),
                    4,
                    "qux",
                    Opcodes.ACC_STATIC,
                    Type.getType("Lorg/eolang/jeo/representation/directives;")
                )
            ).xml(),
            XhtmlMatchers.hasXPath(
                "./o[contains(@base, param)]/o[contains(@base, 'string') and contains(@name, 'type')]"
            )
        );
    }

    @Test
    void generatesParamDirectivesWithAnnotations() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the parameter directives will be generated with annotations",
            new Xembler(
                new DirectivesMethodParams(
                    Collections.singletonList(
                        new DirectivesMethodParam(
                            new Format(),
                            5,
                            "quux",
                            Opcodes.ACC_ABSTRACT,
                            Type.FLOAT_TYPE
                        )
                    ),
                    Collections.singletonList(
                        new DirectivesAnnotations(
                            "annotations", new DirectivesAnnotation("Ljava/lang/Override;", true)
                        )
                    )
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("./o", "param").toXpath(),
                "./o/o[contains(@name, 'annotations')]"
            )
        );
    }
}
