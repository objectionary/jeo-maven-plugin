/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesMethodParam}.
 * @since 0.9
 */
final class DirectivesMethodParamTest {

    @Test
    void generatesParamDirectivesWithSimpleName() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the parameter directives will be generated with a simple name that includes the name",
            new Xembler(
                new DirectivesMethodParam(
                    1, "foo", Opcodes.ACC_PUBLIC, Type.INT_TYPE, new DirectivesAnnotations()
                )
            ).xml(),
            XhtmlMatchers.hasXPath("./o[contains(@base, param) and contains(@as, 'foo')]")
        );
    }

    @Test
    void generatesParamDirectivesWithIndex() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the parameter directives will be generated with an index",
            new Xembler(
                new DirectivesMethodParam(
                    2, "bar", Opcodes.ACC_PRIVATE, Type.DOUBLE_TYPE, new DirectivesAnnotations()
                )
            ).xml(),
            XhtmlMatchers.hasXPath(
                "./o[contains(@base, param)]/o[contains(@base, 'number') and contains(@as, 'index')]"
            )
        );
    }

    @Test
    void generatesPramDirectivesWithAccessModifier() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the parameter directives will be generated with an access modifier",
            new Xembler(
                new DirectivesMethodParam(
                    3, "baz", Opcodes.ACC_PROTECTED, Type.LONG_TYPE, new DirectivesAnnotations()
                )
            ).xml(),
            XhtmlMatchers.hasXPath(
                "./o[contains(@base, param)]/o[contains(@base, 'number') and contains(@as, 'access')]"
            )
        );
    }

    @Test
    void generatesParamDirectivesWithType() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the parameter directives will be generated with a type",
            new Xembler(
                new DirectivesMethodParam(
                    4,
                    "qux",
                    Opcodes.ACC_STATIC,
                    Type.getType("Lorg/eolang/jeo/representation/directives;"),
                    new DirectivesAnnotations()
                )
            ).xml(),
            XhtmlMatchers.hasXPath(
                "./o[contains(@base, param)]/o[contains(@base, 'string') and contains(@as, 'type')]"
            )
        );
    }

    @Test
    void generatesParamDirectivesWithAnnotations() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the parameter directives will be generated with annotations",
            new Xembler(
                new DirectivesMethodParam(
                    5,
                    "quux",
                    Opcodes.ACC_ABSTRACT,
                    Type.FLOAT_TYPE,
                    new DirectivesAnnotations(
                        "annotations", new DirectivesAnnotation("Ljava/lang/Override;", true)
                    )
                )
            ).xml(),
            XhtmlMatchers.hasXPath(
                "./o[contains(@base, param)]/o[contains(@base, 'seq.of') and contains(@as, 'annotations')]"
            )
        );
    }
}
