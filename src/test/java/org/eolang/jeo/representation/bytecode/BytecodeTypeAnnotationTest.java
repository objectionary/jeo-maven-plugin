/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Collections;
import org.eolang.jeo.representation.directives.DirectivesTypeAnnotation;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Tests for {@link BytecodeTypeAnnotation}.
 *
 * @since 0.15.0
 */
final class BytecodeTypeAnnotationTest {

    @Test
    void convertsToDirectives() throws ImpossibleModificationException {
        final int ref = TypeReference.FIELD;
        final TypePath path = TypePath.fromString("*");
        final String desc = "Lorg/eolang/jeo/Directives;";
        final boolean visible = true;
        final Format format = new Format();
        final BytecodePlainAnnotationValue value = new BytecodePlainAnnotationValue(
            "format", "json"
        );
        final int index = 42;
        MatcherAssert.assertThat(
            "We expect the bytecode type annotation to be converted to directives type annotation",
            new Xembler(
                new BytecodeTypeAnnotation(
                    ref,
                    path,
                    desc,
                    visible,
                    Collections.singletonList(
                        value
                    )
                ).directives(index, format)
            ).xml(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesTypeAnnotation(
                        format,
                        index,
                        ref,
                        path.toString(),
                        desc,
                        visible,
                        Collections.singletonList(
                            value.directives(0, format)
                        )
                    )
                ).xml()
            )
        );
    }

    @Test
    void writesAbsentPathAsEmptyOne() throws ImpossibleModificationException {
        final Format format = new Format();
        MatcherAssert.assertThat(
            "A type annotation right on the type has no path, it must be written as the empty one",
            new Xembler(
                new BytecodeTypeAnnotation(
                    TypeReference.newTypeReference(TypeReference.FIELD).getValue(),
                    null,
                    "LNonNull;",
                    true,
                    Collections.emptyList()
                ).directives(0, format)
            ).xml(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesTypeAnnotation(
                        format,
                        0,
                        TypeReference.newTypeReference(TypeReference.FIELD).getValue(),
                        "",
                        "LNonNull;",
                        true,
                        Collections.emptyList()
                    )
                ).xml()
            )
        );
    }
}
