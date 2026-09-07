/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.Arrays;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.VerifiedBytecode;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.xmir.XmlObject;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for the module directives and the module-info round-trip.
 * @since 0.15.0
 */
final class DirectivesModuleTest {

    @Test
    void convertsRequiredModuleToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new DirectivesModuleRequired(new Format(), "module", 1, "0.1.0")
        ).xml();
        MatcherAssert.assertThat(
            "We expect the required module to keep its access, version and name",
            xml,
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "required").toXpath(),
                "/o[contains(@name,'required')]",
                "/o/o[@name='access']",
                "/o/o[@name='version']",
                "/o/o[@name='module']"
            )
        );
    }

    @Test
    void convertsExportedModuleToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new DirectivesModuleExported(new Format(), "com.example", 1, Arrays.asList("m1"))
        ).xml();
        MatcherAssert.assertThat(
            "We expect the exported module to keep its package, access and target modules",
            xml,
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "exported").toXpath(),
                "/o[contains(@name,'exported')]",
                "/o/o[@name='package']",
                "/o/o[@name='access']",
                "/o/o[@name='modules']"
            )
        );
    }

    @Test
    void convertsOpenedModuleToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new DirectivesModuleOpened(new Format(), "com.example", 1, Arrays.asList("m1"))
        ).xml();
        MatcherAssert.assertThat(
            "We expect the opened module to keep its package, access and target modules",
            xml,
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "opened").toXpath(),
                "/o[contains(@name,'opened')]",
                "/o/o[@name='package']",
                "/o/o[@name='access']",
                "/o/o[@name='modules']"
            )
        );
    }

    @Test
    void convertsProvidedModuleToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new DirectivesModuleProvided(new Format(), "service", Arrays.asList("p1"))
        ).xml();
        MatcherAssert.assertThat(
            "We expect the provided module to keep its service and providers",
            xml,
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "provided").toXpath(),
                "/o[contains(@name,'provided')]",
                "/o/o[@name='service']",
                "/o/o[@name='providers']"
            )
        );
    }

    @Test
    void convertsModuleInfoInRoundTrip() {
        final byte[] converted = new XmlObject(
            new BytecodeRepresentation(new ResourceOf("open-module-info.class")).toXmir()
        ).bytecode().bytecode().bytes();
        Assertions.assertDoesNotThrow(
            () -> new VerifiedBytecode(converted).verify(),
            "We expect the round-tripped module-info to pass JVM verification"
        );
    }
}
