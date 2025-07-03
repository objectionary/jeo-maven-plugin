/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.stream.Stream;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.eolang.jeo.representation.bytecode.Codec;
import org.eolang.jeo.representation.bytecode.JavaCodec;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesValue}.
 * This class verifies the generation of value directives for various data types,
 * including primitives, strings, and complex objects.
 *
 * @since 0.3.0
 */
@SuppressWarnings("PMD.TooManyMethods")
final class DirectivesValueTest {

    /**
     * Codec for tests.
     */
    private final Codec codec = new JavaCodec();

    @Test
    void convertsInteger() throws ImpossibleModificationException {
        final String xml = new Xembler(new DirectivesValue("access", 42)).xml();
        MatcherAssert.assertThat(
            String.format(
                "We expect that integer value is converted to the correct XMIR, but got the incorrect one: %n%s%n",
                xml
            ),
            xml,
            XhtmlMatchers.hasXPath(
                "./o[contains(@base,'number') and @name='access']/o[contains(@base, 'bytes')]/text()"
            )
        );
    }

    @ParameterizedTest
    @MethodSource("numbers")
    void convertsNumbers(final Number number, final String base, final String bytes) {
        final String xml = new Xembler(new DirectivesValue("access", number)).xmlQuietly();
        MatcherAssert.assertThat(
            String.format(
                "We expect that number value is converted to the correct XMIR, but got the incorrect one: %n%s%n",
                xml
            ),
            xml,
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("./o", base).toXpath(),
                String.format(
                    "./o[@name='access']/o[contains(@base,'number')]/o[contains(@base,'bytes')]/o[text()='%s']",
                    bytes
                )
            )
        );
    }

    @ParameterizedTest
    @MethodSource("integers")
    void convertsIntegers(final Number number, final String bytes) {
        final String xml = new Xembler(new DirectivesValue("access", number)).xmlQuietly();
        MatcherAssert.assertThat(
            String.format(
                "We expect that integer value is converted to the correct XMIR, but got the incorrect one: %n%s%n",
                xml
            ),
            xml,
            XhtmlMatchers.hasXPaths(
                String.format(
                    "./o[contains(@base,'number') and @name='access']/o[contains(@base,'bytes')]/o[text()='%s']",
                    bytes
                )
            )
        );
    }

    @Test
    void convertsLabel() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Converts label to XML",
            new Xembler(
                new DirectivesValue(
                    new BytecodeLabel("some-random")
                ),
                new Transformers.Node()
            ).xml(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("./o", "label").toXpath(),
                "./o/o[contains(@base,'bytes')]/o[text()='73-6F-6D-65-2D-72-61-6E-64-6F-6D']"
            )
        );
    }

    @Test
    void convertsBoolean() {
        MatcherAssert.assertThat(
            "Converts boolean to XML",
            new Xembler(new DirectivesValue(true)).xmlQuietly(),
            XhtmlMatchers.hasXPath(
                "./o[contains(@base,'true')]"
            )
        );
    }

    @MethodSource("types")
    @ParameterizedTest
    void determinesTypeCorrectly(final Object data, final String type) {
        MatcherAssert.assertThat(
            String.format(
                "Expected and actual types differ, the type for '%s' should be '%s'",
                data,
                type
            ),
            new DirectivesValue(data).type(),
            Matchers.equalTo(type)
        );
    }

    @MethodSource("values")
    @ParameterizedTest
    void convertsRawDataIntoHexString(final Object data, final String hex) {
        MatcherAssert.assertThat(
            String.format(
                String.format(
                    "Expected and actual hex values differ, the value for '%s' should be '%s'",
                    data,
                    hex
                )
            ),
            new DirectivesValue(data).hex(this.codec),
            Matchers.equalTo(hex)
        );
    }

    @Test
    void convertsRawPrimitiveDataToHexString() {
        MatcherAssert.assertThat(
            "Expected and actual hex values differ, the value for '10' should be '00 00 00 00 00 00 00 0A'",
            new DirectivesValue(10).hex(this.codec),
            Matchers.equalTo("00-00-00-00-00-00-00-0A")
        );
        MatcherAssert.assertThat(
            "Expected and actual hex values differ, the value for '0.1d' should be '3F B9 99 99 99 99 99 9A'",
            new DirectivesValue(0.1d).hex(this.codec),
            Matchers.equalTo("3F-B9-99-99-99-99-99-9A")
        );
        MatcherAssert.assertThat(
            "Expected and actual hex values differ, the value for '0.1f' should be '3D CC CC CD'",
            new DirectivesValue(0.1f).hex(this.codec),
            Matchers.equalTo("3D-CC-CC-CD")
        );
        MatcherAssert.assertThat(
            "Expected and actual hex values differ, the value for 'true' should be '01'",
            new DirectivesValue(true).hex(this.codec),
            Matchers.equalTo("01-")
        );
        MatcherAssert.assertThat(
            "Expected and actual hex values differ, the value for 'false' should be '00'",
            new DirectivesValue(false).hex(this.codec),
            Matchers.equalTo("00-")
        );
    }

    @Test
    void encodesType() {
        final String value = new DirectivesValue(Type.INT_TYPE).hex(this.codec);
        MatcherAssert.assertThat(
            "Expected and actual hex values differ, the value for 'Type.INT_TYPE' should be '69 6E 74'",
            value,
            Matchers.equalTo("49-")
        );
    }

    @Test
    void createsStringWithQuotedComment() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that string value will be quoted in the comment",
            new Xembler(new DirectivesValue("java/lang/Object")).xml(),
            Matchers.containsString("<!-- \"java/lang/Object\" -->")
        );
    }

    /**
     * Arguments for {@link DirectivesValueTest#determinesTypeCorrectly(Object, String)} test.
     * @return Stream of arguments.
     */
    static Stream<Arguments> types() {
        return Stream.of(
            Arguments.of(1, "number"),
            Arguments.of("Hello!", "string"),
            Arguments.of(new byte[]{1, 2, 3}, "bytes"),
            Arguments.of(new byte[0], "bytes"),
            Arguments.of(true, "bool"),
            Arguments.of(0.1f, "float"),
            Arguments.of(0.1d, "double"),
            Arguments.of(DirectivesValue.class, "class"),
            Arguments.of(' ', "char")
        );
    }

    /**
     * Arguments for {@link DirectivesValueTest#convertsRawDataIntoHexString(Object, String)}.
     * @return Stream of arguments.
     */
    static Stream<Arguments> values() {
        return Stream.of(
            Arguments.of(10, "00-00-00-00-00-00-00-0A"),
            Arguments.of("Hello!", "48-65-6C-6C-6F-21"),
            Arguments.of(new byte[]{1, 2, 3}, "01-02-03"),
            Arguments.of(new byte[0], "--"),
            Arguments.of(true, "01-"),
            Arguments.of(false, "00-"),
            Arguments.of('a', "00-61"),
            Arguments.of(0.1d, "3F-B9-99-99-99-99-99-9A"),
            Arguments.of(
                DirectivesValueTest.class,
                "6F-72-67-2F-65-6F-6C-61-6E-67-2F-6A-65-6F-2F-72-65-70-72-65-73-65-6E-74-61-74-69-6F-6E-2F-64-69-72-65-63-74-69-76-65-73-2F-44-69-72-65-63-74-69-76-65-73-56-61-6C-75-65-54-65-73-74"
            )
        );
    }

    /**
     * Arguments for {@link DirectivesValueTest#convertsNumbers(Number, String, String)}.
     * @return Stream of arguments.
     */
    static Stream<Arguments> numbers() {
        final String same = "3F-F0-00-00-00-00-00-00";
        return Stream.of(
            Arguments.of(1L, "jeo.long", same),
            Arguments.of(1.0f, "jeo.float", same),
            Arguments.of(1.0d, "jeo.double", same),
            Arguments.of((short) 1, "jeo.short", same),
            Arguments.of((byte) 1, "jeo.byte", same)
        );
    }

    /**
     * Arguments for {@link DirectivesValueTest#convertsIntegers(Number, String)} (Number, String)}.
     * @return Stream of arguments.
     */
    static Stream<Arguments> integers() {
        final String same = "3F-F0-00-00-00-00-00-00";
        return Stream.of(
            Arguments.of(1, same),
            Arguments.of(100, "40-59-00-00-00-00-00-00"),
            Arguments.of(1057, "40-90-84-00-00-00-00-00")
        );
    }
}
