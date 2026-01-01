/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.Arrays;
import java.util.Collections;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeRecordComponent;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotations;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.RecordComponentNode;
import org.objectweb.asm.tree.TypeAnnotationNode;

/**
 * Tests for {@link AsmRecordComponents}.
 * @since 0.15.0
 */
final class AsmRecordComponentsTest {

    @Test
    void mapsNullToEmptyList() {
        MatcherAssert.assertThat(
            "We expect that when null is passed to the constructor, the bytecode method returns an empty list",
            new AsmRecordComponents(null).bytecode(),
            Matchers.empty()
        );
    }

    @Test
    void mapsSingleRecordComponentToBytecodeComponent() {
        final String name = "name";
        final String descr = "descriptor";
        final String sign = "signature";
        final RecordComponentNode node = new RecordComponentNode(name, descr, sign);
        MatcherAssert.assertThat(
            "We expect a single record component node to map to a single bytecode component",
            new AsmRecordComponents(
                Collections.singletonList(node)
            ).bytecode(),
            Matchers.equalTo(
                Collections.singletonList(new BytecodeRecordComponent(name, descr, sign))
            )
        );
    }

    @Test
    void mapsMultipleRecordComponentsToBytecodeComponents() {
        final String fname = "name1";
        final String fdescr = "descriptor1";
        final String fsign = "signature1";
        final RecordComponentNode first = new RecordComponentNode(fname, fdescr, fsign);
        final String sname = "name2";
        final String sdescr = "descriptor2";
        final String ssign = "signature2";
        final RecordComponentNode second = new RecordComponentNode(sname, sdescr, ssign);
        MatcherAssert.assertThat(
            "We expect multiple record component nodes to map to corresponding bytecode components",
            new AsmRecordComponents(
                Arrays.asList(first, second)
            ).bytecode(),
            Matchers.equalTo(
                Arrays.asList(
                    new BytecodeRecordComponent(fname, fdescr, fsign),
                    new BytecodeRecordComponent(sname, sdescr, ssign)
                )
            )
        );
    }

    @Test
    void mapsSingleRecordComponentWithAnnotationsToBytecodeComponent() {
        final String name = "name";
        final String descr = "descriptor";
        final String sign = "signature";
        final RecordComponentNode node = new RecordComponentNode(name, descr, sign);
        node.visibleAnnotations = Collections.singletonList(
            new AnnotationNode("Lvisible-annotation;")
        );
        node.invisibleAnnotations = Collections.singletonList(
            new AnnotationNode("Linvisible-annotation;")
        );
        MatcherAssert.assertThat(
            "We expect a single record component node with annotations to map to a bytecode component with corresponding annotations",
            new AsmRecordComponents(
                Collections.singletonList(node)
            ).bytecode(),
            Matchers.equalTo(
                Collections.singletonList(
                    new BytecodeRecordComponent(
                        name,
                        descr,
                        sign,
                        new AsmAnnotations(
                            node.visibleAnnotations,
                            node.invisibleAnnotations
                        ).bytecode(),
                        new BytecodeTypeAnnotations()
                    )
                )
            )
        );
    }

    @Test
    void mapsRecordComponentWithTypeAnnotationsToBytecodeComponent() {
        final String name = "name";
        final String descr = "descriptor";
        final String sign = "signature";
        final RecordComponentNode node = new RecordComponentNode(name, descr, sign);
        final int vref = TypeReference.FIELD;
        final TypePath vpath = TypePath.fromString("*");
        final String vdescr = "Lvisible-type-annotation;";
        node.visibleTypeAnnotations = Collections.singletonList(
            new TypeAnnotationNode(vref, vpath, vdescr)
        );
        final int iref = TypeReference.CLASS_TYPE_PARAMETER;
        final TypePath itype = TypePath.fromString("0;1;");
        final String idescr = "Linvisible-type-annotation;";
        node.invisibleTypeAnnotations = Collections.singletonList(
            new TypeAnnotationNode(iref, itype, idescr)
        );
        MatcherAssert.assertThat(
            "We expect a single record component node with type annotations to map to a bytecode component",
            new AsmRecordComponents(Collections.singletonList(node)).bytecode(),
            Matchers.equalTo(
                Collections.singletonList(
                    new BytecodeRecordComponent(
                        name,
                        descr,
                        sign,
                        new BytecodeAnnotations(),
                        new BytecodeTypeAnnotations(
                            new BytecodeTypeAnnotation(
                                vref, vpath, vdescr, true, Collections.emptyList()
                            ),
                            new BytecodeTypeAnnotation(
                                iref, itype, idescr, false, Collections.emptyList()
                            )
                        )
                    )
                )
            )
        );
    }
}
