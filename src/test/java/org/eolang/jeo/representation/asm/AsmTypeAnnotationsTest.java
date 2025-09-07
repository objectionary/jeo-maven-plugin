/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.Collections;
import org.eolang.jeo.representation.bytecode.BytecodePlainAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotations;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;
import org.objectweb.asm.tree.RecordComponentNode;

/**
 * Test case for {@link AsmTypeAnnotations}.
 * @since 0.15.0
 */
final class AsmTypeAnnotationsTest {

    @Test
    void mapsValuesOfTypeAnnotation() {
        final RecordComponentNode node = new RecordComponentNode(
            "name", "Ljava/lang/String;", null
        );
        final int ref = TypeReference.FIELD;
        final TypePath path = TypePath.fromString("*");
        final String desc = "Lorg/eolang/jeo/SomeAnnotation;";
        final boolean visible = true;
        final AnnotationVisitor visited = node.visitTypeAnnotation(
            new TypeReference(ref).getValue(),
            path,
            desc,
            visible
        );
        final String key = "key";
        final String value = "value";
        visited.visit(key, value);
        visited.visitEnd();
        MatcherAssert.assertThat(
            "We expect the type annotations to be converted to bytecode type annotations",
            new AsmTypeAnnotations(node).bytecode(),
            Matchers.equalTo(
                new BytecodeTypeAnnotations(
                    new BytecodeTypeAnnotation(
                        ref,
                        path,
                        desc,
                        visible,
                        Collections.singletonList(new BytecodePlainAnnotationValue(key, value))
                    )
                )
            )
        );
    }
}
