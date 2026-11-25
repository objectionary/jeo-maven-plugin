/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import com.jcabi.matchers.XhtmlMatchers;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link BytecodeListing}.
 *
 * @since 0.6
 */
final class BytecodeListingTest {

    @Test
    void convertsToPrettyString() throws Exception {
        MatcherAssert.assertThat(
            "Bytecode listing is not as expected",
            new BytecodeListing(
                new BytesOf(new ResourceOf("MethodByte.class")).asBytes()
            ).toString(),
            Matchers.containsString("org/eolang/jeo/MethodByte")
        );
    }

    @Test
    void escapesRestrictedXmlCharactersInSourceSignatureAndLocalNames() {
        MatcherAssert.assertThat(
            "Listing should escape control characters outside string constants",
            new BytecodeListing(0, BytecodeListingTest.classWithRestrictedChars()).toString(),
            Matchers.allOf(
                Matchers.containsString("src\\u0001.java"),
                Matchers.containsString("debug\\u0002info"),
                Matchers.containsString("\\u0003"),
                Matchers.containsString("loc\\u0004")
            )
        );
    }

    @Test
    void disassemblesWithOmitListingsFalseDespiteRestrictedChars() {
        MatcherAssert.assertThat(
            "Disassembling with omitListings=false should produce valid XMIR with listing element",
            new BytecodeRepresentation(new Bytecode(BytecodeListingTest.classWithRestrictedChars()))
                .toXmir(new Format(Format.WITH_LISTING, true))
                .toString(),
            XhtmlMatchers.hasXPath("/object/listing")
        );
    }

    @Test
    void escapesRestrictedControlRangesInListing() {
        MatcherAssert.assertThat(
            "Listing should escape U+007F-U+0084 and U+0086-U+009F control characters",
            new BytecodeListing(0, BytecodeListingTest.classWithControlRanges()).toString(),
            Matchers.allOf(
                Matchers.containsString("del\\u007f_end\\u0084.java"),
                Matchers.containsString("c1\\u0086_\\u009f")
            )
        );
    }

    @Test
    void disassemblesRestrictedControlRangesToXmir() {
        MatcherAssert.assertThat(
            "Disassembling with omitListings=false should succeed for U+007F-U+009F",
            new BytecodeRepresentation(new Bytecode(BytecodeListingTest.classWithControlRanges()))
                .toXmir(new Format(Format.WITH_LISTING, true))
                .toString(),
            XhtmlMatchers.hasXPath("/object/listing")
        );
    }

    private static byte[] classWithControlRanges() {
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(
            Opcodes.V17,
            Opcodes.ACC_PUBLIC,
            "ControlRangeListing",
            null,
            "java/lang/Object",
            null
        );
        writer.visitSource(
            String.format("del%c_end%c.java", 0x7F, 0x84),
            String.format("c1%c_%c", 0x86, 0x9F)
        );
        writer.visitEnd();
        return writer.toByteArray();
    }

    private static byte[] classWithRestrictedChars() {
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(
            Opcodes.V17,
            Opcodes.ACC_PUBLIC,
            "ControlListing",
            null,
            "java/lang/Object",
            null
        );
        writer.visitSource(
            String.format("src%c.java", 1),
            String.format("debug%cinfo", 2)
        );
        writer.visitField(
            Opcodes.ACC_PRIVATE,
            "fld",
            "Ljava/lang/Object;",
            String.format("Ljava/util/List<La%cb;>;", 3),
            null
        ).visitEnd();
        final MethodVisitor method = writer.visitMethod(
            Opcodes.ACC_PUBLIC,
            "run",
            "()V",
            null,
            null
        );
        method.visitCode();
        final Label start = new Label();
        final Label end = new Label();
        method.visitLabel(start);
        method.visitInsn(Opcodes.RETURN);
        method.visitLabel(end);
        method.visitLocalVariable(String.format("loc%c", 4), "I", null, start, end, 1);
        method.visitMaxs(0, 2);
        method.visitEnd();
        writer.visitEnd();
        return writer.toByteArray();
    }
}
