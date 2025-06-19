/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XMLDocument;
import java.util.Collections;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeClassProperties;
import org.eolang.jeo.representation.bytecode.InnerClass;
import org.eolang.jeo.representation.xmir.NativeXmlNode;
import org.eolang.jeo.representation.xmir.XmlClass;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesClass}.
 * This class verifies the generation of class directives,
 * including class properties, inner classes, and bytecode conversion.
 *
 * @since 0.1.0
 */
final class DirectivesClassTest {

    @Test
    void createsWithSimpleConstructor() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create class with simple constructor",
            new Xembler(
                new DirectivesClass(
                    new ClassName("Neo"),
                    new DirectivesClassProperties(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    new DirectivesAnnotations(
                        "annotations", new DirectivesAnnotation("Override", true)
                    ),
                    new DirectivesAttributes(
                        new InnerClass("Inner", "Outer", "Inner", 0).directives()
                    )
                ),
                new Transformers.Node()
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "/o[@name='Neo']",
                "/o[@name='Neo']/o[contains(@as,'version')]",
                "/o[@name='Neo']/o[contains(@as,'access')]",
                "/o[@name='Neo']/o[contains(@as,'signature')]",
                "/o[@name='Neo']/o[contains(@as,'supername')]",
                "/o[@name='Neo']/o[contains(@as,'interfaces')]",
                "/o[@name='Neo']/o[contains(@as,'annotations')]",
                "/o[@name='Neo']/o[contains(@as,'attributes')]"
            )
        );
    }

    @Test
    void appendsField() {
        final String xml = new Xembler(
            new DirectivesClass(new ClassName("Neo"), new DirectivesField()),
            new Transformers.Node()
        ).xmlQuietly();
        MatcherAssert.assertThat(
            String.format(
                "Can't append field to the class; result is: '%s'",
                new XMLDocument(xml)
            ),
            xml,
            XhtmlMatchers.hasXPath("/o[@name='Neo']/o[contains(@base,'field')]")
        );
    }

    @Test
    void appendsMethod() {
        final String xml = new Xembler(
            new DirectivesClass(new ClassName("Neo"), new DirectivesMethod("method")),
            new Transformers.Node()
        ).xmlQuietly();
        MatcherAssert.assertThat(
            String.format(
                "Can't append method to the class, or the method is appended wrongly; result is: '%s'",
                new XMLDocument(xml)
            ),
            xml,
            XhtmlMatchers.hasXPath("/o[@name='Neo']/o[contains(@as,'method')]")
        );
    }

    @Test
    void convertsToDirectives() throws ImpossibleModificationException {
        final String name = "Foo";
        final int access = 100;
        final String signature = "java/lang/Object";
        final String supername = "java/lang/Runnable";
        final String interfce = "java/lang/Cloneable";
        MatcherAssert.assertThat(
            "We expect that class created from directives is equal to expected",
            new XmlClass(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesClass(
                            name,
                            new DirectivesClassProperties(
                                access,
                                signature,
                                supername,
                                interfce
                            )
                        )
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeClass(
                    name,
                    new BytecodeClassProperties(access, signature, supername, interfce)
                )
            )
        );
    }

}
