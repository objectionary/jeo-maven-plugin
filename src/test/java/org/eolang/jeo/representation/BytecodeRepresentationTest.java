/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.VerifiedBytecode;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.asm.AsmUnknownAttribute;
import org.eolang.jeo.representation.asm.AsmUnknownAttributes;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.bytecode.BytecodeUnknownAttribute;
import org.eolang.jeo.representation.bytecode.EoCodec;
import org.eolang.jeo.representation.directives.DirectivesValue;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

/**
 * Test cases for {@link BytecodeRepresentation}.
 * This class verifies the parsing and conversion of Java bytecode
 * to EO representation, including name extraction and XML generation.
 *
 * @since 0.1.0
 */
final class BytecodeRepresentationTest {

    /**
     * The name of the resource with the simplest class.
     */
    @SuppressWarnings("JTCOP.RuleProhibitStaticFields")
    private static final String METHOD_BYTE = "MethodByte.class";

    /**
     * Format for debug mode.
     */
    @SuppressWarnings("JTCOP.RuleProhibitStaticFields")
    private static final Format DEBUG = new Format(Format.MODE, "debug");

    /**
     * XPath of type annotations nested in a local variable table entry.
     */
    @SuppressWarnings("JTCOP.RuleProhibitStaticFields")
    private static final String VARIABLE_TYPES =
        "//o[o[contains(@base, 'local-variable')]]/o[@name='type-annotations']";

    @ParameterizedTest
    @ValueSource(strings = {"count", "Φξ"})
    void preservesRegisteredFieldAttributes(final String name) {
        final byte[] payload = {1, 2, 3};
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(
            Opcodes.V1_8, Opcodes.ACC_PUBLIC, "example/FieldAttributes",
            null, "java/lang/Object", null
        );
        final FieldVisitor field = writer.visitField(
            Opcodes.ACC_PUBLIC, name, "I", null, null
        );
        field.visitAttribute(new AsmUnknownAttribute("ScalaSig", payload));
        field.visitEnd();
        writer.visitEnd();
        final ClassNode restored = new ClassNode();
        new ClassReader(
            new XmirRepresentation(
                new BytecodeRepresentation(new Bytecode(writer.toByteArray())).toXmir()
            ).toBytecode().bytes()
        ).accept(restored, AsmUnknownAttributes.prototypes(), 0);
        MatcherAssert.assertThat(
            "Registered custom field attribute bytes must survive disassembly and assembly",
            new AsmUnknownAttributes(restored.fields.get(0)).bytecode(),
            Matchers.contains(new BytecodeUnknownAttribute("ScalaSig", payload))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"plain", "a+b", "a%41b", "x%2Fy", "100%", "two words", "café+50%"})
    void preservesNamesWhenReassembling(final String name) {
        final String fullname = String.format("example/%s", name);
        final String type = String.format("L%s;", fullname);
        final String signature = String.format("(%s)%s", type, type);
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(
            Opcodes.V1_8, Opcodes.ACC_PUBLIC, fullname, null, "java/lang/Object", null
        );
        writer.visitField(Opcodes.ACC_PUBLIC, name, type, null, null).visitEnd();
        final MethodVisitor method = writer.visitMethod(
            Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, name, signature, null, null
        );
        method.visitCode();
        method.visitVarInsn(Opcodes.ALOAD, 0);
        method.visitInsn(Opcodes.ARETURN);
        method.visitMaxs(1, 1);
        method.visitEnd();
        writer.visitEnd();
        final ClassNode restored = new ClassNode();
        new ClassReader(
            new XmirRepresentation(
                new BytecodeRepresentation(new Bytecode(writer.toByteArray())).toXmir()
            ).toBytecode().bytes()
        ).accept(restored, 0);
        MatcherAssert.assertThat(
            "Class, field and method names and descriptors must survive the XMIR round trip",
            Arrays.asList(
                restored.name, restored.fields.get(0).name, restored.fields.get(0).desc,
                restored.methods.get(0).name, restored.methods.get(0).desc
            ),
            Matchers.contains(fullname, name, type, name, signature)
        );
    }

    @Test
    void keepsParameterAnnotationsUnderArabicLocale() throws Exception {
        final Locale before = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("ar-EG"));
        try {
            MatcherAssert.assertThat(
                "Parameter annotations must survive a round trip in any default locale",
                new XmirRepresentation(
                    new BytecodeRepresentation(
                        new Bytecode(
                            new BytesOf(new ResourceOf("AnnotationsApplication.class")).asBytes()
                        )
                    ).toXmir(new Format(Format.MODE, "debug"))
                ).toBytecode().toString(),
                Matchers.containsString(
                    "@Lorg/eolang/jeo/annotations/ParamAnnotation;(value=\"custom\") // parameter 0"
                )
            );
        } finally {
            Locale.setDefault(before);
        }
    }

    @Test
    void writesListingWithTheDetailOfTheModel() {
        MatcherAssert.assertThat(
            "In short mode the listing must not show debug information the XMIR does not keep",
            new BytecodeRepresentation(new ResourceOf(BytecodeRepresentationTest.METHOD_BYTE))
                .toXmir(new Format(Format.WITH_LISTING, true, Format.MODE, "short"))
                .xpath("/object/listing/text()")
                .get(0),
            Matchers.not(Matchers.containsString("LINENUMBER"))
        );
    }

    @Test
    void parsesBytecode() {
        MatcherAssert.assertThat(
            "The simplest class should contain the object with MethodByte name",
            new BytecodeRepresentation(new ResourceOf(BytecodeRepresentationTest.METHOD_BYTE))
                .toXmir().xpath("/object/o/@name").get(0),
            Matchers.equalTo(new PrefixedName("MethodByte").encode())
        );
    }

    @Test
    void generatesXmlWithCorrectObjectAttributes() {
        MatcherAssert.assertThat(
            "XMIR objects should have either 'name' or 'base' attribute, or both",
            new BytecodeRepresentation(
                new ResourceOf(BytecodeRepresentationTest.METHOD_BYTE)
            ).toXmir().toString(),
            Matchers.not(
                XhtmlMatchers.hasXPath(
                    "//o[not(@base) and not(@name) and not(parent::*[@base='Φ.bytes'])]"
                )
            )
        );
    }

    /**
     * This test was added to mitigate the issue:
     * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1160">Issue #1160</a>.
     */
    @Test
    void generatesXmlWithFormationsThatCIncludeObjectsWithNames() {
        MatcherAssert.assertThat(
            "A formation must only include objects with names.",
            new BytecodeRepresentation(
                new ResourceOf(BytecodeRepresentationTest.METHOD_BYTE)
            ).toXmir().toString(),
            Matchers.not(XhtmlMatchers.hasXPath("//o[not(@base)]/o[not(@name)]"))
        );
    }

    @Test
    void retrievesName() {
        final String actual = new BytecodeRepresentation(
            new ResourceOf(BytecodeRepresentationTest.METHOD_BYTE)
        ).name();
        final String expected = "org/eolang/jeo/MethodByte";
        MatcherAssert.assertThat(
            String.format(
                "The name should be retrieved without exceptions and equal to the expected '%s'",
                expected
            ),
            actual,
            Matchers.equalTo(expected)
        );
    }

    /**
     * This test checks that the bytecode representation have a stack with a double value.
     * The test was added to mitigate the issue:
     * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1211:">Issue #1211</a>.
     */
    @Test
    void parsesBytecodeWithDoubleStack() {
        MatcherAssert.assertThat(
            "we expect to find a frame with a double stack",
            new BytecodeRepresentation(
                new ResourceOf("Example.class")
            ).toXmir(),
            XhtmlMatchers.hasXPath(
                String.format(
                    "//o[contains(@name, 'stack')]/o[@name='x0']/o/o[text()='%s']",
                    new DirectivesValue("double").hex(new EoCodec())
                )
            )
        );
    }

    /**
     * This test was added to mitigate the issue with stack map frames mapping.
     * You can read more about it here:
     * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1215">Issue</a>
     *
     * @throws Exception if something goes wrong
     */
    @Test
    void parsesBytecodeLoggerStackFrames() throws Exception {
        final ResourceOf input = new ResourceOf("LoggerFactory$DelegatingLogger.class");
        MatcherAssert.assertThat(
            "The disassembled/assembled bytecode representation should match the original bytecode",
            new Bytecode(
                new XmirRepresentation(
                    new BytecodeRepresentation(input).toXmir(BytecodeRepresentationTest.DEBUG)
                ).toBytecode().bytes()
            ).toString(),
            Matchers.equalTo(new Bytecode(new BytesOf(input).asBytes()).toString())
        );
    }

    /**
     * LtIncorrectUnlint.class has a nullable param name, see issue #1233.
     */
    @Test
    void parsesBytecodeWithNullParameterName() {
        Assertions.assertDoesNotThrow(
            () -> new BytecodeRepresentation(
                new ResourceOf("LtIncorrectUnlint.class")
            ).toXmir(),
            "We expect to parse a bytecode with a null name without exceptions"
        );
    }

    /**
     * This bug was found in the issue:
     * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1282">#1282</a>
     * The problem lies in the 'signature' field of the LocalVariableNode,
     * if it is null, we save an empty string in XMIR and then try to decode it back into bytecode,
     * which leads to saving an empty signature in the variable table of the method,
     * instead of null.
     * You can find the fix here:
     * {@link  org.eolang.jeo.representation.bytecode.LocalVariable#write(MethodVisitor, AsmLabels)}
     *
     * @throws Exception In case of error
     */
    @Test
    void doesNotAddEmptySignaturesToVariableTable() throws Exception {
        final ResourceOf input = new ResourceOf("Check.class");
        MatcherAssert.assertThat(
            "The disassembled/assembled bytecode representation should match the original bytecode",
            new Bytecode(
                new XmirRepresentation(
                    new BytecodeRepresentation(input).toXmir(BytecodeRepresentationTest.DEBUG)
                ).toBytecode().bytes()
            ).toString(),
            Matchers.equalTo(new Bytecode(new BytesOf(input).asBytes()).toString())
        );
    }

    @Test
    void parsesModuleInfo() throws Exception {
        final ResourceOf resource = new ResourceOf("closed-module-info.class");
        MatcherAssert.assertThat(
            "The module-info class should be parsed correctly",
            new XmirRepresentation(
                new BytecodeRepresentation(resource).toXmir(BytecodeRepresentationTest.DEBUG)
            ).toBytecode().toString(),
            Matchers.equalTo(new Bytecode(new BytesOf(resource).asBytes()).toString())
        );
    }

    @Test
    void parsesModuleInfoAndConvertsItBackToValidBytecode() {
        final Bytecode debug = new XmirRepresentation(
            new BytecodeRepresentation(
                new ResourceOf("closed-module-info.class")
            ).toXmir(BytecodeRepresentationTest.DEBUG)
        ).toBytecode();
        Assertions.assertDoesNotThrow(
            () -> new VerifiedBytecode(debug.bytes()).verify(),
            "We expect to convert the module-info class back to valid bytecode"
        );
    }

    @Test
    void parsesAnnotationsAndConvertsThemBackToValidBytecode() throws Exception {
        final Bytecode original = new Bytecode(
            new BytesOf(new ResourceOf("AnnotationsApplication.class")).asBytes()
        );
        MatcherAssert.assertThat(
            "The disassembled/assembled bytecode representation should match the original bytecode",
            new XmirRepresentation(
                new BytecodeRepresentation(
                    original
                ).toXmir(BytecodeRepresentationTest.DEBUG)
            ).toBytecode().toString(),
            Matchers.equalTo(original.toString())
        );
    }

    /**
     * This test mitigates the issue
     * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1925">#1925</a>:
     * type annotations that live inside the Code attribute (casts, instance checks,
     * object creations, local variables and catch parameters) were lost by a round trip.
     *
     * @throws Exception In case of error
     */
    @Test
    void keepsTypeAnnotationsInsideMethodBodies() throws Exception {
        MatcherAssert.assertThat(
            "All seven type annotations of the method body must survive the round trip",
            BytecodeRepresentationTest.bodyTypeAnnotations(
                new XmirRepresentation(
                    new BytecodeRepresentation(
                        new ResourceOf("TypeAnnotations.class")
                    ).toXmir(BytecodeRepresentationTest.DEBUG)
                ).toBytecode()
            ),
            Matchers.equalTo(7)
        );
    }

    /**
     * This test mitigates the issue
     * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1925">#1925</a>:
     * keeping the type annotations of the Code attribute must not change
     * the bytecode of the class during a round trip.
     *
     * @throws Exception In case of error
     */
    @Test
    void keepsMethodBodyIntactWithAnnotations() throws Exception {
        final ResourceOf input = new ResourceOf("TypeAnnotations.class");
        MatcherAssert.assertThat(
            "The disassembled/assembled bytecode representation should match the original bytecode",
            new XmirRepresentation(
                new BytecodeRepresentation(input).toXmir(BytecodeRepresentationTest.DEBUG)
            ).toBytecode().toString(),
            Matchers.equalTo(new Bytecode(new BytesOf(input).asBytes()).toString())
        );
    }

    @Test
    void writesTypeAnnotationsNextToTheirEntries() {
        MatcherAssert.assertThat(
            "XMIR must contain type annotations of instructions, try-catch blocks and local variables",
            new BytecodeRepresentation(
                new ResourceOf("TypeAnnotations.class")
            ).toXmir(BytecodeRepresentationTest.DEBUG).toString(),
            XhtmlMatchers.hasXPaths(
                "//o[@name='type-annotations']",
                "//o[o[contains(@base, 'opcode')] and o[@name='type-annotations']]",
                "//o[o[contains(@base, 'trycatch')] and o[@name='type-annotations']]"
            )
        );
    }

    /**
     * TypeAnnotations.class is compiled without the local variable table, so
     * only the detached path (a type annotation written next to the label that
     * opens its scope) is covered by the tests above. This fixture is compiled
     * with {@code -g}: its local variable annotations match local variable
     * table entries and must be nested in the `local-variable` attributes.
     */
    @Test
    void writesTypeAnnotationsInsideLocalVariableEntries() {
        MatcherAssert.assertThat(
            "XMIR must nest type annotations of local variables under their local-variable entries",
            new BytecodeRepresentation(
                new ResourceOf("TypeAnnotationsLocalVariable.class")
            ).toXmir(BytecodeRepresentationTest.DEBUG).toString(),
            XhtmlMatchers.hasXPath(
                String.format(
                    "%s/o[starts-with(@name, 'type-annotation')]",
                    BytecodeRepresentationTest.VARIABLE_TYPES
                )
            )
        );
    }

    /**
     * TypeAnnotations.class is compiled without the local variable table, so
     * only the detached path is covered by the tests above. This fixture is
     * compiled with {@code -g}: its local variable annotations match local
     * variable table entries and must survive the round trip.
     *
     * @throws Exception In case of error
     */
    @Test
    void keepsTypeAnnotationsOfLocalVariablesInsideTableEntries() throws Exception {
        final ResourceOf input = new ResourceOf("TypeAnnotationsLocalVariable.class");
        MatcherAssert.assertThat(
            "The disassembled/assembled bytecode representation should match the original bytecode",
            new XmirRepresentation(
                new BytecodeRepresentation(input).toXmir(BytecodeRepresentationTest.DEBUG)
            ).toBytecode().toString(),
            Matchers.equalTo(new Bytecode(new BytesOf(input).asBytes()).toString())
        );
    }

    /**
     * A local variable with a split live range is described by several local
     * variable table entries, while ASM models its type annotation with
     * parallel start/end/index lists: one annotation with several ranges.
     * The ranges of one annotation must stay together after the round trip,
     * otherwise the reassembled class file contains extra annotation entries.
     *
     * @throws Exception In case of error
     */
    @Test
    void keepsMultiRangeLocalVariableAnnotationIntact() throws Exception {
        final ResourceOf input = new ResourceOf("TypeAnnotationsMultiRange.class");
        MatcherAssert.assertThat(
            "The disassembled/assembled bytecode representation should match the original bytecode",
            new XmirRepresentation(
                new BytecodeRepresentation(input).toXmir(BytecodeRepresentationTest.DEBUG)
            ).toBytecode().toString(),
            Matchers.equalTo(new Bytecode(new BytesOf(input).asBytes()).toString())
        );
    }

    /**
     * A multi-range local variable annotation must carry all its ranges in
     * XMIR, not only the first one.
     */
    @Test
    void writesAllRangesOfMultiRangeLocalVariableAnnotation() {
        MatcherAssert.assertThat(
            "A multi-range local variable annotation must carry both scopes in XMIR",
            new BytecodeRepresentation(
                new ResourceOf("TypeAnnotationsMultiRange.class")
            ).toXmir(BytecodeRepresentationTest.DEBUG).toString(),
            XhtmlMatchers.hasXPaths(
                BytecodeRepresentationTest.VARIABLE_TYPES,
                String.format(
                    "%s/o/o[@name='scope-start']",
                    BytecodeRepresentationTest.VARIABLE_TYPES
                ),
                String.format(
                    "%s/o/o[@name='scope-end']",
                    BytecodeRepresentationTest.VARIABLE_TYPES
                ),
                String.format(
                    "%s/o/o[@name='scope-slot']",
                    BytecodeRepresentationTest.VARIABLE_TYPES
                )
            )
        );
    }

    private static int bodyTypeAnnotations(final Bytecode bytecode) {
        final ClassNode parsed = new ClassNode();
        new ClassReader(bytecode.bytes()).accept(parsed, 0);
        int total = 0;
        for (final MethodNode method : parsed.methods) {
            for (final AbstractInsnNode instruction : method.instructions) {
                total += BytecodeRepresentationTest.size(instruction.visibleTypeAnnotations);
                total += BytecodeRepresentationTest.size(instruction.invisibleTypeAnnotations);
            }
            total += BytecodeRepresentationTest.size(method.visibleLocalVariableAnnotations);
            total += BytecodeRepresentationTest.size(method.invisibleLocalVariableAnnotations);
            for (final TryCatchBlockNode block : method.tryCatchBlocks) {
                total += BytecodeRepresentationTest.size(block.visibleTypeAnnotations);
                total += BytecodeRepresentationTest.size(block.invisibleTypeAnnotations);
            }
        }
        return total;
    }

    private static int size(final List<?> list) {
        final int result;
        if (list == null) {
            result = 0;
        } else {
            result = list.size();
        }
        return result;
    }
}
