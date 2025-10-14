/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.Collections;
import org.eolang.jeo.representation.NumberedName;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.JavaCodec;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesMethod}.
 * This class verifies the generation of method directives for Java methods,
 * including method properties, annotations, and bytecode instructions.
 *
 * @since 0.5.0
 */
final class DirectivesMethodTest {

    @Test
    void addsPrefixToTheMethodName() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that 'jm$' prefix will be added to the method name",
            new Xembler(new BytecodeMethod("φTerm").directives(1)).xml(),
            XhtmlMatchers.hasXPaths("./o[contains(@name, 'jm$φTerm')]")
        );
    }

    @Test
    void savesMethodNameAsAttributeValue() {
        final String foo = "foo";
        MatcherAssert.assertThat(
            "We expect that the method name will be saved as an attribute value",
            new Xembler(new DirectivesMethod(foo)).xmlQuietly(),
            XhtmlMatchers.hasXPath(
                String.format(
                    "./o[contains(@name,'%s')]/o[@name='name']/o/o[text()='%s']",
                    foo,
                    new DirectivesValue(0, new Format(), foo).hex(new JavaCodec())
                )
            )
        );
    }

    @Test
    void createsEmptyXmirIfMethodIsEmpty() {
        final String xml = new Xembler(new DirectivesMethod("foo")).xmlQuietly();
        MatcherAssert.assertThat(
            String.format(
                "We expect that empty method won't contain any redundant directives, generated: %n%s%n",
                xml
            ),
            xml,
            Matchers.not(
                Matchers.anyOf(
                    XhtmlMatchers.hasXPath("./o[contains(@base,'method')]/o[contains(@as,'body')]"),
                    XhtmlMatchers.hasXPath(
                        "./o[contains(@base,'method')]/o[contains(@as,'exceptions')]"
                    ),
                    XhtmlMatchers.hasXPath(
                        "./o[contains(@base,'method')]/o[contains(@as,'params')]"
                    ),
                    XhtmlMatchers.hasXPath(
                        "./o[contains(@base,'method')]/o[contains(@as,'annotations')]"
                    ),
                    XhtmlMatchers.hasXPath(
                        "./o[contains(@base,'method')]/o[contains(@as,'trycatchblocks')]"
                    ),
                    XhtmlMatchers.hasXPath(
                        "./o[contains(@base,'method')]/o[contains(@as,'attributes')]"
                    )
                )
            )
        );
    }

    /**
     * This test was added to check if the #1063 issue is fixed.
     * You can read more about it
     * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1063">here</a>
     */
    @Test
    void generatesMethodWithSimpleBodyName() throws ImpossibleModificationException {
        final String descriptor = "()I";
        final Format format = new Format();
        MatcherAssert.assertThat(
            "We expect that the method body name will be generated correctly without any suffixes and prefixes",
            new Xembler(
                new DirectivesMethod(
                    format,
                    new NumberedName(1, "checks1063"),
                    new DirectivesMethodProperties(1, descriptor, ""),
                    Collections.singletonList(new DirectivesInstruction(0, format, Opcodes.RETURN)),
                    Collections.emptyList(),
                    new DirectivesAnnotations(),
                    Collections.emptyList(),
                    new DirectivesAttributes()
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "method").toXpath(),
                "./o[contains(@name,'checks1063')]/o[@name='body']"
            )
        );
    }

    @Test
    void convertsToEmptyAnnotationsIfThereAreNoAnnotations() {
        MatcherAssert.assertThat(
            "We expect that empty annotations will be mentioned in the method directives",
            new Xembler(new DirectivesMethod("foo")).xmlQuietly(),
            XhtmlMatchers.hasXPath(
                "./o[contains(@name,'foo')]/o[contains(@name,'annotations')]"
            )
        );
    }
}
