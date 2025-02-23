/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package it;

import com.jcabi.xml.XML;
import java.io.IOException;
import org.cactoos.io.ResourceOf;
import org.eolang.parser.EoSyntax;

/**
 * EO source.
 * @since 0.1
 */
@SuppressWarnings("JTCOP.RuleCorrectTestName")
final class EoSource {

    /**
     * Resource of EO program.
     */
    private final String resource;

    /**
     * Constructor.
     * @param resource Resource of EO program.
     */
    EoSource(final String resource) {
        this.resource = resource;
    }

    /**
     * Parse the EO program into XMIR.
     * @return XMIR.
     */
    XML parse() {
        try {
            return new EoSyntax("scenario", new ResourceOf(this.resource)).parsed();
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't parse '%s'", this.resource),
                exception
            );
        }
    }
}
