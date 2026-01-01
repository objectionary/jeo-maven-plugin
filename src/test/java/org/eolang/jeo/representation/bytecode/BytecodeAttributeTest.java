/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eolang.jeo.representation.directives.DirectivesAnnotations;
import org.eolang.jeo.representation.directives.DirectivesEnclosingMethod;
import org.eolang.jeo.representation.directives.DirectivesNestHost;
import org.eolang.jeo.representation.directives.DirectivesNestMembers;
import org.eolang.jeo.representation.directives.DirectivesPermittedSubclasses;
import org.eolang.jeo.representation.directives.DirectivesRecordComponent;
import org.eolang.jeo.representation.directives.DirectivesRecordComponents;
import org.eolang.jeo.representation.directives.DirectivesSourceFile;
import org.eolang.jeo.representation.directives.DirectivesTypeAnnotations;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link BytecodeAttribute}.
 * @since 0.14.0
 */
final class BytecodeAttributeTest {

    @Test
    void convertsSourceFileToDirectives() throws ImpossibleModificationException {
        final String source = "source";
        final String debug = "debug";
        MatcherAssert.assertThat(
            "We expect to have proper XML representation for SourceFile",
            new Xembler(
                new BytecodeAttribute.SourceFile(source, debug).directives(0, new Format())
            ).xml(),
            Matchers.equalTo(
                new Xembler(new DirectivesSourceFile(new Format(), source, debug)).xml()
            )
        );
    }

    @Test
    void convertsEnclosingMethodToDirectives() throws ImpossibleModificationException {
        final String owner = "owner";
        final String method = "method";
        final String descriptor = "descriptor";
        MatcherAssert.assertThat(
            "We expect to have proper XML representation for EnclosingMethod",
            new Xembler(
                new BytecodeAttribute.EnclosingMethod(owner, method, descriptor)
                    .directives(0, new Format())
            ).xml(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesEnclosingMethod(
                        new Format(),
                        owner,
                        method,
                        descriptor
                    )
                ).xml()
            )
        );
    }

    @Test
    void convertsNestHostToDirectives() throws ImpossibleModificationException {
        final String host = "host";
        MatcherAssert.assertThat(
            "We expect to have proper XML representation for NestHost",
            new Xembler(
                new BytecodeAttribute.NestHost(host).directives(0, new Format())
            ).xml(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesNestHost(
                        new Format(),
                        host
                    )
                ).xml()
            )
        );
    }

    @Test
    void convertsNestMembersToDirectives() throws ImpossibleModificationException {
        final List<String> members = Arrays.asList("member1", "member2");
        MatcherAssert.assertThat(
            "We expect to have proper XML representation for NestMembers",
            new Xembler(
                new BytecodeAttribute.NestMembers(members).directives(0, new Format())
            ).xml(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesNestMembers(
                        new Format(),
                        members
                    )
                ).xml()
            )
        );
    }

    @Test
    void convertsPermittedSubclassesToDirectives() throws ImpossibleModificationException {
        final List<String> subclasses = Arrays.asList("subclass1", "subclass2");
        MatcherAssert.assertThat(
            "We expect to have proper XML representation for PermittedSubclasses",
            new Xembler(
                new BytecodeAttribute.PermittedSubclasses(subclasses).directives(0, new Format())
            ).xml(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesPermittedSubclasses(
                        new Format(),
                        subclasses
                    )
                ).xml()
            )
        );
    }

    @Test
    void covertsRecordComponentsToDirectives() throws ImpossibleModificationException {
        final Format format = new Format();
        final int index = 0;
        MatcherAssert.assertThat(
            "We expect to have proper XML representation for RecordComponents",
            new Xembler(
                new BytecodeAttribute.RecordComponents(
                    new BytecodeRecordComponent("n", "d", "s")
                ).directives(index, format)
            ).xml(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesRecordComponents(
                        index,
                        Collections.singletonList(
                            new DirectivesRecordComponent(
                                format,
                                0,
                                "n",
                                "d",
                                "s",
                                new DirectivesAnnotations(),
                                new DirectivesTypeAnnotations()
                            )
                        )
                    )
                ).xml()
            )
        );
    }

}
