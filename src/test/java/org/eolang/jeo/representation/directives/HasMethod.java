/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.xml.XMLDocument;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.HexData;
import org.eolang.jeo.representation.PrefixedName;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.objectweb.asm.Label;

/**
 * Matcher to check if the received XMIR document has a method inside a class with a given name.
 * @since 0.1.0
 */
@SuppressWarnings({
    "PMD.TooManyMethods",
    "PMD.AvoidAccessToStaticMembersViaThis",
    "JTCOP.RuleAllTestsHaveProductionClass",
    "JTCOP.RuleCorrectTestName",
    "JTCOP.RuleInheritanceInTests"
})
public final class HasMethod extends TypeSafeMatcher<String> {

    /**
     * Class name.
     */
    private final String clazz;

    /**
     * Method name.
     */
    private final String name;

    /**
     * Method parameters.
     */
    private final List<String> params;

    /**
     * Method instructions.
     */
    private final List<HasInstruction> instr;

    /**
     * Method labels.
     */
    private final List<HasLabel> lbls;

    /**
     * Method try catch entry.
     */
    private final List<HasTryCatch> trycatches;

    /**
     * Constructor.
     * @param method Method name.
     */
    public HasMethod(final String method) {
        this("", method);
    }

    /**
     * Constructor.
     * @param clazz Class name.
     * @param method Method name.
     */
    private HasMethod(final String clazz, final String method) {
        this.clazz = clazz;
        this.name = method;
        this.params = new ArrayList<>(0);
        this.instr = new ArrayList<>(0);
        this.lbls = new ArrayList<>(0);
        this.trycatches = new ArrayList<>(0);
    }

    @Override
    public boolean matchesSafely(final String item) {
        final XMLDocument document = new XMLDocument(item);
        return this.checks().stream().map(document::xpath).noneMatch(List::isEmpty);
    }

    @Override
    public void describeTo(final Description description) {
        final Description descr = description.appendText(
            "Received XMIR document doesn't comply with the following XPaths: "
        );
        this.checks().forEach(xpath -> descr.appendText("\n").appendText(xpath));
        descr.appendText("\nPlease, check the received XMIR document.");
    }

    /**
     * Inside a class.
     * @param klass Class name.
     * @return New matcher that checks class.
     */
    public HasMethod inside(final String klass) {
        return new HasMethod(new PrefixedName(klass).encode(), this.name);
    }

    /**
     * With parameter.
     * @param parameter Parameter name.
     * @return The same matcher that checks parameter.
     */
    public HasMethod withParameter(final String parameter) {
        this.params.add(parameter);
        return this;
    }

    /**
     * With instruction.
     * @param opcode Opcode.
     * @param args Arguments.
     * @return The same matcher that checks instruction.
     */
    public HasMethod withInstruction(final int opcode, final Object... args) {
        this.instr.add(new HasInstruction(opcode, args));
        return this;
    }

    /**
     * With label.
     * @return The same matcher that checks label.
     */
    public HasMethod withLabel() {
        this.lbls.add(new HasLabel());
        return this;
    }

    /**
     * With try-catch.
     * @param type Exception type.
     * @return The same matcher that checks try-catch.
     */
    public HasMethod withTryCatch(final String type) {
        this.trycatches.add(new HasTryCatch(type));
        return this;
    }

    /**
     * Checks.
     * @return List of XPaths to check.
     */
    private List<String> checks() {
        return Stream.concat(
            Stream.concat(
                this.definition(),
                this.parameters()
            ),
            Stream.concat(
                this.trycatch(),
                Stream.concat(
                    this.instructions(),
                    this.labels()
                )
            )
        ).collect(Collectors.toList());
    }

    /**
     * Definition xpaths.
     * @return List of XPaths to check.
     */
    private Stream<String> definition() {
        final String root = this.root();
        return Stream.of(
            root.concat("/@name"),
            root.concat("/o[contains(@base,'seq')]/@base")
        );
    }

    /**
     * Parameters xpaths.
     * @return List of XPaths to check.
     */
    private Stream<String> parameters() {
        final String root = this.root();
        return this.params.stream()
            .map(
                param -> String.format(
                    "%s/o[contains(@base,'params')]/o[@name='%s' and contains(@base,'param')]/@name",
                    root,
                    param
                )
            );
    }

    /**
     * Instructions xpaths.
     * @return List of XPaths to check.
     */
    private Stream<String> instructions() {
        final String root = this.root();
        return this.instr.stream()
            .flatMap(instruction -> instruction.checks(root));
    }

    /**
     * Try-catch xpaths.
     * @return List of XPaths to check.
     */
    private Stream<String> trycatch() {
        final String root = this.root();
        return this.trycatches.stream()
            .flatMap(trycatch -> trycatch.checks(root));
    }

    /**
     * Labels xpaths.
     * @return List of XPaths to check.
     */
    private Stream<String> labels() {
        final String root = this.root();
        return this.lbls.stream()
            .flatMap(label -> HasLabel.checks(root));
    }

    /**
     * Root XPath.
     * @return XPath.
     */
    private String root() {
        return String.format(
            "/program/objects/o[@name='%s']/o[contains(@name,'%s')]",
            this.clazz,
            this.name
        );
    }

    /**
     * Instruction checks.
     *
     * @since 0.1.0
     */
    private static final class HasInstruction {

        /**
         * Opcode.
         */
        private final int opcode;

        /**
         * Arguments.
         */
        private final List<Object> args;

        /**
         * Constructor.
         * @param opcode Opcode.
         * @param args Arguments.
         */
        HasInstruction(final int opcode, final Object... args) {
            this(opcode, Arrays.asList(args));
        }

        /**
         * Constructor.
         * @param opcode Opcode.
         * @param args Arguments.
         */
        HasInstruction(final int opcode, final List<Object> args) {
            this.opcode = opcode;
            this.args = args;
        }

        /**
         * Checks of instruction.
         * @param root Root Method XPath.
         * @return List of XPaths to check.
         */
        Stream<String> checks(final String root) {
            final String instruction = String.format(
                "%s/o[contains(@base,'seq') and @name='@']/o[contains(@base,'opcode') and contains(@name,'%s')]",
                root,
                new OpcodeName(this.opcode).simplified()
            );
            return Stream.concat(
                Stream.of(
                    instruction.concat("/@base"),
                    String.format(
                        "%s/o[contains(@base,'int') and @data='bytes' and text()='%s']/@base",
                        instruction,
                        new HexData(this.opcode).value()
                    )
                ),
                this.arguments(instruction)
            );
        }

        /**
         * Checks of arguments.
         * @param instruction Root Instruction XPath.
         * @return List of XPaths to check.
         */
        private Stream<String> arguments(final String instruction) {
            return this.args.stream()
                .map(
                    arg -> {
                        final String result;
                        final HexData hex = new HexData(arg);
                        if (arg instanceof Label) {
                            result = String.format(
                                "%s/o[contains(@base,'%s') and @data='bytes']/@data",
                                instruction,
                                hex.type()
                            );
                        } else {
                            result = String.format(
                                "%s/o[contains(@base,'%s') and @data='bytes' and text()='%s']/@data",
                                instruction,
                                hex.type(),
                                hex.value()
                            );
                        }
                        return result;
                    }
                );
        }
    }

    /**
     * Try-catch checks.
     * @since 0.1
     */
    private static final class HasTryCatch {

        /**
         * Exception type.
         */
        private final String type;

        /**
         * Constructor.
         * @param exception Exception type.
         */
        private HasTryCatch(final String exception) {
            this.type = exception;
        }

        /**
         * XPaths to check.
         * @param root Root Method XPath.
         * @return List of XPaths to check.
         */
        Stream<String> checks(final String root) {
            return Stream.of(
                String.format("%s/@base", HasTryCatch.path(root)),
                String.format(
                    "%s/o[1][contains(@base,'label')]/@base", HasTryCatch.path(root)
                ),
                String.format(
                    "%s/o[2][contains(@base,'label')]/@base", HasTryCatch.path(root)
                ),
                String.format(
                    "%s/o[3][contains(@base,'label')]/@base", HasTryCatch.path(root)
                ),
                String.format(
                    "%s/o[4][contains(@base,'string')]/@base", HasTryCatch.path(root)
                ),
                String.format(
                    "%s/o[4][contains(@base,'string') and text()='%s']/@data",
                    HasTryCatch.path(root),
                    new HexData(this.type).value()
                )
            );
        }

        /**
         * Try-catch entry xpath.
         * @param root Root Method XPath.
         * @return XPath.
         */
        private static String path(final String root) {
            return String.format(
                "%s/o[contains(@base,'seq') and contains(@name, 'trycatchblocks')]/o[contains(@base,'trycatch')]",
                root
            );
        }
    }

    /**
     * Checks that Xmir contains at least one label in the method.
     * Pay Attention! That we just can't verify exact id's of labels since ASM library doesn't
     * allow it. Hence, we can just check the presence of a label.
     * @since 0.1
     */
    @SuppressWarnings("PMD.UseUtilityClass")
    private static final class HasLabel {

        /**
         * Checks of label.
         * @param root Root Method XPath.
         * @return List of XPaths to check.
         */
        static Stream<String> checks(final String root) {
            return Stream.of(
                String.format(
                    "%s/o[contains(@base,'seq') and @name='@']/o[contains(@base,'label') and @data='bytes']/@data",
                    root
                )
            );
        }
    }
}
