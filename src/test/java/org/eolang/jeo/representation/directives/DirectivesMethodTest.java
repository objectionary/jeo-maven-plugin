/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.Collections;
import org.eolang.jeo.representation.NumberedName;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeMaxs;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.BytecodeMethodProperties;
import org.eolang.jeo.representation.xmir.NativeXmlNode;
import org.eolang.jeo.representation.xmir.XmlMethod;
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
    void transformsToXmir() throws ImpossibleModificationException {
        final String name = "Hello";
        final int access = 100;
        final String descriptor = "()I";
        final String signature = "";
        MatcherAssert.assertThat(
            "We expect that directives will generate correct method",
            new XmlMethod(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesMethod(
                            name,
                            new DirectivesMethodProperties(access, descriptor, signature)
                        )
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeMethod(
                    new BytecodeMethodProperties(name, descriptor, signature, access),
                    new BytecodeMaxs()
                )
            )
        );
    }

    @Test
    void transformsAnnotationsToXmir() throws ImpossibleModificationException {
        final String descriptor = "Consumer";
        final boolean visible = true;
        final String name = "foo";
        MatcherAssert.assertThat(
            "We expect that annotation dirictes will be added to the method",
            new XmlMethod(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesMethod(name)
                            .withAnnotation(new DirectivesAnnotation(descriptor, visible))
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeMethod(
                    name,
                    new BytecodeAnnotations(
                        new BytecodeAnnotation(descriptor, visible)
                    )
                )
            )
        );
    }

    @Test
    void addsPrefixToTheMethodName() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that 'j$' prefix will be added to the method name",
            new Xembler(new BytecodeMethod("φTerm").directives(1)).xml(),
            XhtmlMatchers.hasXPaths("./o[contains(@as, 'j$φTerm')]")
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
        MatcherAssert.assertThat(
            "We expect that the method body name will be generated correctly without any suffixes and prefixes",
            new Xembler(
                new DirectivesMethod(
                    new NumberedName(1, "checks1063"),
                    new DirectivesMethodProperties(1, descriptor, ""),
                    Collections.singletonList(new DirectivesInstruction(Opcodes.RETURN)),
                    Collections.emptyList(),
                    new DirectivesAnnotations(),
                    Collections.emptyList(),
                    new DirectivesAttributes()
                )
            ).xml(),
            XhtmlMatchers.hasXPath(
                "./o[contains(@base,'method') and contains(@as,'checks1063')]/o[@as='body']"
            )
        );
    }
}
