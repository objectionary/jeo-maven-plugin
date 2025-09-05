/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Collections;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodePlainAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeRecordComponent;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotations;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlRecordComponent}.
 * @since 0.15.0
 */
final class XmlRecordComponentTest {

    @Test
    void parsesRecordComponent() throws ImpossibleModificationException {
        final BytecodeRecordComponent original = new BytecodeRecordComponent(
            "name",
            "descr",
            null,
            new BytecodeAnnotations(),
            new BytecodeTypeAnnotations()
        );
        final String xml = new Xembler(original.directives(0, new Format())).xml();
        MatcherAssert.assertThat(
            "We expect the record component to be parsed correctly",
            new XmlRecordComponent(new JcabiXmlNode(xml)).bytecode(),
            Matchers.equalTo(original)
        );
    }

    @Test
    void parsesRecordComponentWithBytecodeAnnotations() throws ImpossibleModificationException {
        final BytecodeRecordComponent original = new BytecodeRecordComponent(
            "name",
            "descr",
            null,
            new BytecodeAnnotations(
                Collections.singletonList(
                    new BytecodeAnnotation(
                        "Lorg/eolang/jeo/Directives;",
                        true,
                        Collections.singletonList(
                            new BytecodePlainAnnotationValue("key", "value")
                        )
                    )
                )
            ),
            new BytecodeTypeAnnotations()
        );
        MatcherAssert.assertThat(
            "We expect the record component with bytecode annotations to be parsed correctly",
            new XmlRecordComponent(
                new JcabiXmlNode(
                    new Xembler(original.directives(0, new Format())).xml()
                )
            ).bytecode(),
            Matchers.equalTo(original)
        );
    }

    @Test
    void parsesRecordComponentWithTypeAnnotations() throws ImpossibleModificationException {
        final BytecodeRecordComponent original = new BytecodeRecordComponent(
            "name",
            "descr",
            null,
            new BytecodeAnnotations(),
            new BytecodeTypeAnnotations(
                Collections.singletonList(
                    new BytecodeTypeAnnotation(
                        TypeReference.FIELD,
                        TypePath.fromString("*"),
                        "Lorg/eolang/jeo/Directives;",
                        true,
                        Collections.singletonList(
                            new BytecodePlainAnnotationValue("key", "value")
                        )
                    )
                )
            )
        );
        MatcherAssert.assertThat(
            "We expect the record component with type annotations to be parsed correctly",
            new XmlRecordComponent(
                new JcabiXmlNode(
                    new Xembler(original.directives(0, new Format())).xml()
                )
            ).bytecode(),
            Matchers.equalTo(original)
        );
    }

}
