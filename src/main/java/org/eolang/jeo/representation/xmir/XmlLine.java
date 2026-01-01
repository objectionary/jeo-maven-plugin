/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeLine;

/**
 * This class represents a line in the XML representation of bytecode.
 * <p>
 *     Mirrors {@link org.eolang.jeo.representation.directives.DirectivesLine}.
 * </p>
 * @since 0.14.0
 */
final class XmlLine implements XmlBytecodeEntry {

    /**
     * XML representation of a line.
     */
    private final XmlJeoObject object;

    /**
     * XML representation of a line.
     * @param node XML node representing the line.
     */
    XmlLine(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * XML representation of a line.
     * @param object XML object representing the line.
     */
    XmlLine(final XmlJeoObject object) {
        this.object = object;
    }

    @Override
    public BytecodeLine bytecode() {
        return new BytecodeLine(
            (int) new XmlValue(
                this.object.child(0)
                    .orElseThrow(
                        () -> new IllegalStateException(
                            "Line node must have the first child with a number"
                        )
                    )
            ).object(),
            this.object.child(1)
                .map(XmlLabel::new)
                .orElseThrow(
                    () -> new IllegalStateException(
                        "Line node must have a label as the second child"
                    )
                ).bytecode()
        );
    }
}
