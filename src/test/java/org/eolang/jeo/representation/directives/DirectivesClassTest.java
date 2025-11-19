/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XMLDocument;
import java.util.Collections;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.bytecode.InnerClass;
import org.eolang.jeo.representation.bytecode.JavaCodec;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
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
        final Format format = new Format();
        MatcherAssert.assertThat(
            "Can't create class with simple constructor",
            new Xembler(
                new DirectivesClass(
                    format,
                    new ClassName("Neo"),
                    new DirectivesClassProperties(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    "",
                    new DirectivesAnnotations(
                        "annotations", new DirectivesAnnotation("Override", true)
                    ),
                    new DirectivesAttributes(
                        new InnerClass("Inner", "Outer", "Inner", 0).directives(0, format)
                    )
                ),
                new Transformers.Node()
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "/o[contains(@name,'Neo')]",
                "/o[contains(@name,'Neo')]/o[contains(@name,'version')]",
                "/o[contains(@name,'Neo')]/o[contains(@name,'access')]",
                "/o[contains(@name,'Neo')]/o[contains(@name,'signature')]",
                "/o[contains(@name,'Neo')]/o[contains(@name,'supername')]",
                "/o[contains(@name,'Neo')]/o[contains(@name,'interfaces')]",
                "/o[contains(@name,'Neo')]/o[contains(@name,'annotations')]",
                "/o[contains(@name,'Neo')]/o[contains(@name,'attributes')]"
            )
        );
    }

    @Test
    void appendsField() throws ImpossibleModificationException {
        final DirectivesField field = new DirectivesField();
        final String xml = new Xembler(
            new DirectivesClass(new ClassName("Neo"), field),
            new Transformers.Node()
        ).xml();
        MatcherAssert.assertThat(
            "Can't append field to the class",
            xml,
            Matchers.containsString(
                new Xembler(field, new Transformers.Node()).xml()
            )
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
            XhtmlMatchers.hasXPath("/o[@name='j$Neo']/o[contains(@name,'method')]")
        );
    }

    @Test
    void appendsFullyQualifiedName() throws ImpossibleModificationException {
        final String name = "org.eolang.jeo.representation.directives.DirectivesClassTest";
        MatcherAssert.assertThat(
            "Can't append fully qualified class name",
            new Xembler(
                new DirectivesClass(
                    new ClassName(name),
                    new DirectivesClassProperties(name)
                )
            ).xml(),
            XhtmlMatchers.hasXPath(
                String.format(
                    "./o[contains(@name,DirectivesClassTest)]/o[contains(@name, 'name')]/o/o[text()='%s']",
                    new DirectivesValue(0, new Format(), name.replace('.', '/'))
                        .hex(new JavaCodec())
                )
            )
        );
    }

    @Test
    void createsDirectivesWithMandatorySignature() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create proper xml with mandatory signature",
            new Xembler(
                new Directives()
                    .add("o")
                    .append(
                        new DirectivesClass(
                            "MyClass",
                            "org/eolang/SomeClass",
                            new DirectivesClassProperties()
                        )
                    ).up()
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "//o[contains(@name,'signature')]"
            )
        );
    }

    /**
     * This test was added to ensure that when a null signature is passed,
     * it is converted to an empty string in the XML output.
     * See more details in
     * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1246">#1246</a>
     * @throws ImpossibleModificationException in case of XML modification failure.
     */
    @Test
    void convertsNullSignatureToEmptyString() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Signature should be empty string when null is passed",
            new Xembler(
                new Directives().add("o").append(
                    new DirectivesClass(
                        "MyClass",
                        null,
                        new DirectivesClassProperties()
                    )
                ).up()
            ).xml(),
            XhtmlMatchers.hasXPath(
                "//o[@name='signature']/o[contains(@base,'bytes')]/o[text()='--']"
            )
        );
    }
}
