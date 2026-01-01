/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.eolang.jeo.representation.bytecode.BytecodeRecordComponent;
import org.eolang.jeo.representation.bytecode.InnerClass;
import org.eolang.jeo.representation.directives.DirectivesAttribute;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlAttribute}.
 * @since 0.6
 */
final class XmlAttributeTest {

    @Test
    void convertsToDomainInnerClass() throws ImpossibleModificationException {
        final BytecodeAttribute expected = new InnerClass("name", "outer", "inner", 0);
        MatcherAssert.assertThat(
            "We expect the attribute to be converted to a correct  domain attribute (inner class)",
            new XmlAttribute(new Xembler(expected.directives(0, new Format())).xml()).attribute(),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void throwsExceptionIfCannotIdentifyAttribute() {
        MatcherAssert.assertThat(
            "We expect an exception message be understandable and clear",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new XmlAttribute(
                    new Xembler(new DirectivesAttribute("unknown", "0")).xmlQuietly()
                ).attribute(),
                "We expect an exception to be thrown if the attribute cannot be identified"
            ).getMessage(),
            Matchers.equalTo("Unknown attribute base 'Φ.jeo.unknown'")
        );
    }

    @Test
    void parsesSourceFile() {
        final String source = "source";
        final String debug = "debug";
        final BytecodeAttribute.SourceFile attr = new BytecodeAttribute.SourceFile(source, debug);
        MatcherAssert.assertThat(
            "We expect to parse SourceFile attribute",
            new XmlAttribute(
                new Xembler(attr.directives(0, new Format())).xmlQuietly()
            ).attribute(),
            Matchers.equalTo(attr)
        );
    }

    @Test
    void parsesEnclosingMethod() {
        final String owner = "owner";
        final String method = "method";
        final String descriptor = "descriptor";
        final BytecodeAttribute attr = new BytecodeAttribute.EnclosingMethod(
            owner, method, descriptor
        );
        MatcherAssert.assertThat(
            "We expect to parse EnclosingMethod attribute",
            new XmlAttribute(
                new Xembler(attr.directives(0, new Format())).xmlQuietly()
            ).attribute(),
            Matchers.equalTo(attr)
        );
    }

    @Test
    void parsesNestHost() {
        final BytecodeAttribute attr = new BytecodeAttribute.NestHost("host");
        MatcherAssert.assertThat(
            "We expect to parse NestHost attribute",
            new XmlAttribute(
                new Xembler(attr.directives(0, new Format())).xmlQuietly()
            ).attribute(),
            Matchers.equalTo(attr)
        );
    }

    @Test
    void parsesNestMembers() {
        final BytecodeAttribute attr = new BytecodeAttribute.NestMembers(
            "member1",
            "member2"
        );
        MatcherAssert.assertThat(
            "We expect to parse NestMembers attribute",
            new XmlAttribute(
                new Xembler(attr.directives(0, new Format())).xmlQuietly()
            ).attribute(),
            Matchers.equalTo(attr)
        );
    }

    @Test
    void parsesPermittedSubclasses() {
        final BytecodeAttribute attr = new BytecodeAttribute.PermittedSubclasses(
            "subclass1", "subclass2"
        );
        MatcherAssert.assertThat(
            "We expect to parse PermittedSubclasses attribute",
            new XmlAttribute(
                new Xembler(attr.directives(0, new Format())).xmlQuietly()
            ).attribute(),
            Matchers.equalTo(attr)
        );
    }

    @Test
    void parsesRecordComponents() {
        final BytecodeAttribute attr = new BytecodeAttribute.RecordComponents(
            new BytecodeRecordComponent("name", "descr", null)
        );
        MatcherAssert.assertThat(
            "We expect to parse RecordComponents attribute",
            new XmlAttribute(
                new Xembler(attr.directives(0, new Format())).xmlQuietly()
            ).attribute(),
            Matchers.equalTo(attr)
        );
    }
}
