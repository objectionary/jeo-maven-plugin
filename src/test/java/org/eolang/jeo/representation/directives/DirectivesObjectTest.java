/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesObject}.
 * @since 0.1
 */
final class DirectivesObjectTest {

    @Test
    void createsCorrectDirectives() {
        final ClassName name = new ClassName("Foo");
        final String actual = new Xembler(
            new DirectivesObject(new DirectivesClass(name), new DirectivesMetas(name)),
            new Transformers.Node()
        ).xmlQuietly();
        MatcherAssert.assertThat(
            String.format(
                "Can't correctly and without errors create object directives, received XML: %n%s",
                new XMLDocument(actual)
            ),
            actual,
            XhtmlMatchers.hasXPath("/object/o[@name='Foo']")
        );
    }

    @Test
    void setsVersion() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that version and revision number will be set from MANIFEST.MF file",
            new Xembler(
                new BytecodeProgram(
                    new BytecodeClass("Some")
                ).directives("")
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "/object[@version='1.2.3']",
                "/object[@revision='1234567']",
                "/object[@dob='2023-03-19T00:00:00']"
            )
        );
    }

    @Test
    void setsMilliseconds() throws ImpossibleModificationException {
        final ClassName clazz = new ClassName("Some");
        final DirectivesObject object = new DirectivesObject(
            "some code",
            10,
            new DirectivesClass(clazz),
            new DirectivesMetas(clazz)
        );
        MatcherAssert.assertThat(
            "We expect that milliseconds will be set",
            new Xembler(object).xml(),
            XhtmlMatchers.hasXPath("/object[@ms='10']")
        );
    }
}
