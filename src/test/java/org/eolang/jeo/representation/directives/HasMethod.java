/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.xml.XMLDocument;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.PrefixedName;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.eolang.jeo.representation.bytecode.JavaCodec;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Hamcrest matcher to verify that an XMIR document contains a method with a specific name.
 * This matcher supports complex method validation including parameters, return types,
 * and method body verification for comprehensive testing.
 *
 * @since 0.1.0
 * @todo #1130:90min Don't use {@link HasMethod} class in 'Bytecode' tests.
 *  We should compare generated XMIR with expected XMIR using 'Directives' classes instead.
 *  Otherwise, we have to invent such overly complicated classes as {@link HasMethod}.
 *  When we will have proper XMIR comparison, we can remove this class.
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
            root.concat("/@as"),
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
                    "%s/o[contains(@base,'params')]/o[contains(@as,'%s') and contains(@base,'param')]/@as",
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
            "/object/o[contains(@name,'%s')]/o[contains(@as,'%s')]",
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
                "%s/o[contains(@base,'seq') and contains(@as,'body')]/o[contains(@base,'opcode')]",
                root
            );
            return Stream.concat(
                Stream.of(
                    instruction.concat("/@base"),
                    String.format(
                        "%s/o[contains(@base,'number')]/o[contains(@base,'bytes')]/o[text()='%s']/text()",
                        instruction,
                        new DirectivesValue((double) this.opcode).hex(new JavaCodec())
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
                        if (arg instanceof Number) {
                            final DirectivesValue simple = new DirectivesValue(
                                arg
                            );
                            final DirectivesValue hex = new DirectivesValue(
                                ((Number) arg).doubleValue()
                            );
                            result = String.format(
                                "%s/o[contains(@base,'%s')]/o[contains(@base,'bytes')]/o[text()='%s']/text()",
                                instruction,
                                simple.type(),
                                hex.hex(new JavaCodec())
                            );
                        } else if (arg instanceof BytecodeLabel) {
                            final DirectivesValue hex = new DirectivesValue(arg);
                            result = String.format(
                                "%s/o[contains(@base,'%s')]/o[contains(@base,'bytes')]/@base",
                                instruction,
                                hex.type()
                            );
                        } else {
                            final DirectivesValue hex = new DirectivesValue(arg);
                            result = String.format(
                                "%s/o[contains(@base,'%s')]/o[contains(@base,'bytes')]/o[text()='%s']/text()",
                                instruction,
                                hex.type(),
                                hex.hex(new JavaCodec())
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
                    "%s/o[4][contains(@base,'string')]/o[1]/o[text()='%s']/text()",
                    HasTryCatch.path(root),
                    new DirectivesValue(this.type).hex(new JavaCodec())
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
                "%s/o[contains(@base,'seq') and contains(@as, 'trycatchblocks')]/o[contains(@base,'trycatch')]",
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
                    "%s/o[contains(@base,'seq') and contains(@as,'body')]/o[contains(@base,'label')]/o[contains(@base, 'org.eolang.bytes')]/@base",
                    root
                )
            );
        }
    }
}
