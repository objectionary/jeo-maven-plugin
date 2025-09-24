/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import com.jcabi.matchers.XhtmlMatchers;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.VerifiedBytecode;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.bytecode.EoCodec;
import org.eolang.jeo.representation.directives.DirectivesValue;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.MethodVisitor;

/**
 * Test cases for {@link BytecodeRepresentation}.
 * This class verifies the parsing and conversion of Java bytecode
 * to EO representation, including name extraction and XML generation.
 *
 * @since 0.1.0
 */
@SuppressWarnings("PMD.TooManyMethods")
final class BytecodeRepresentationTest {

    /**
     * Bytecode example that contains a double value in the stack.
     */
    private static final String EXAMPLE_BYTECODE = "Example.class";

    /**
     * The name of the resource with the simplest class.
     */
    private static final String METHOD_BYTE = "MethodByte.class";

    /**
     * The example of bytecode with a nullable param name.
     * You can read more about it here:
     * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1233">#1233</a>
     */
    private static final String NULL_NAME = "LtIncorrectUnlint.class";

    /**
     * Format for debug mode.
     */
    private static final Format DEBUG = new Format(Format.MODE, "debug");

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
                    "//o[not(@base) and not(@name) and not(parent::*[@base='Q.org.eolang.bytes'])]"
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
        final ResourceOf input = new ResourceOf(BytecodeRepresentationTest.METHOD_BYTE);
        final String actual = new BytecodeRepresentation(input).name();
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
                new ResourceOf(BytecodeRepresentationTest.EXAMPLE_BYTECODE)
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
     * @throws Exception if something goes wrong
     */
    @Test
    void parsesBytecodeLoggerStackFrames() throws Exception {
        final ResourceOf input = new ResourceOf("LoggerFactory$DelegatingLogger.class");
        final String actual = new Bytecode(
            new XmirRepresentation(
                new BytecodeRepresentation(input).toXmir(BytecodeRepresentationTest.DEBUG)
            ).toBytecode().bytes()
        ).toString();
        final String expected = new Bytecode(new BytesOf(input).asBytes()).toString();
        MatcherAssert.assertThat(
            "The disassembled/assembled bytecode representation should match the original bytecode",
            actual,
            Matchers.equalTo(expected)
        );
    }

    @Test
    void parsesBytecodeWithNullParameterName() {
        Assertions.assertDoesNotThrow(
            () -> new BytecodeRepresentation(
                new ResourceOf(BytecodeRepresentationTest.NULL_NAME)
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
     * @throws Exception In case of error.
     */
    @Test
    void doesNotAddEmptySignaturesToVariableTable() throws Exception {
        final ResourceOf input = new ResourceOf("Check.class");
        final String actual = new Bytecode(
            new XmirRepresentation(
                new BytecodeRepresentation(input).toXmir(BytecodeRepresentationTest.DEBUG)
            ).toBytecode().bytes()
        ).toString();
        final String expected = new Bytecode(new BytesOf(input).asBytes()).toString();
        MatcherAssert.assertThat(
            "The disassembled/assembled bytecode representation should match the original bytecode",
            actual,
            Matchers.equalTo(expected)
        );
    }

    @Test
    void parsesModuleInfo() throws Exception {
        final ResourceOf resource = new ResourceOf("closed-module-info.class");
        final Bytecode expected = new Bytecode(new BytesOf(resource).asBytes());
        MatcherAssert.assertThat(
            "The module-info class should be parsed correctly",
            new XmirRepresentation(
                new BytecodeRepresentation(resource).toXmir(BytecodeRepresentationTest.DEBUG)
            ).toBytecode().toString(),
            Matchers.equalTo(expected.toString())
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
}
