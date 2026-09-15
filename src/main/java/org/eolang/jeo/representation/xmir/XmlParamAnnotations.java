/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eolang.jeo.representation.bytecode.BytecodeParamAnnotations;

/**
 * Mirrors {@link org.eolang.jeo.representation.bytecode.BytecodeParamAnnotations}.
 *
 * @since 0.15.0
 */
public final class XmlParamAnnotations {

    /**
     * The shape of a name that holds a group of parameter annotations.
     */
    private static final Pattern NAME = Pattern.compile("param-annotations-(\\d+)");

    /**
     * Xmir node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     *
     * @param node Xmir node
     */
    XmlParamAnnotations(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Convert to bytecode.
     *
     * @return Bytecode parameter annotations
     */
    public BytecodeParamAnnotations bytecode() {
        final Matcher matcher = XmlParamAnnotations.NAME.matcher(this.node.name());
        if (!matcher.matches()) {
            throw new IllegalStateException(
                String.format(
                    "The name '%s' is not a group of parameter annotations, '%s' is expected",
                    this.node.name(), XmlParamAnnotations.NAME.pattern()
                )
            );
        }
        return new BytecodeParamAnnotations(
            Integer.parseInt(matcher.group(1)),
            new XmlAnnotations(this.node).bytecode()
        );
    }

    /**
     * Whether this node represents parameter annotations.
     *
     * @return True if it does
     */
    boolean isParamAnnotations() {
        return XmlParamAnnotations.NAME.matcher(this.node.name()).matches();
    }
}
