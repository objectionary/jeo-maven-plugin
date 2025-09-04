/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
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
        final DirectivesTypeAnnotation directives = new BytecodeTypeAnnotation(
            ref,
            path,
            desc,
            visible,
            Collections.singletonList(
                value
            )
        ).directives(index, format);
        MatcherAssert.assertThat(
            "We expect the bytecode type annotation to be converted to directives type annotation",
            new Xembler(directives).xml(),
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
}
