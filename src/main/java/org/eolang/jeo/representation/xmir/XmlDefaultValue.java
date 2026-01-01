/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Optional;
import org.eolang.jeo.representation.bytecode.BytecodeDefaultValue;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.directives.JeoFqn;

/**
 * XMIR of annotation default value.
 * @since 0.3
 */
public final class XmlDefaultValue {

    /**
     * Annotation default value fully qualified name.
     */
    private static final String ADEFVALUE = new JeoFqn("annotation-default-value").fqn();

    /**
     * Default value XMIR node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param node Default value XMIR node.
     */
    XmlDefaultValue(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * Constructor.
     * @param node XML Jeo object node.
     */
    private XmlDefaultValue(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Convert to bytecode.
     * @return Bytecode default value.
     */
    public Optional<BytecodeDefaultValue> bytecode() {
        return this.node.children().findFirst().map(
            property -> new BytecodeDefaultValue(
                new XmlAnnotationValue(property).bytecode()
            )
        );
    }

    /**
     * Write to method.
     * @param method Method.
     */
    public void writeTo(final BytecodeMethod method) {
        this.bytecode().ifPresent(method::defvalue);
    }

    /**
     * Is default value?
     * @return True this node is default value.
     */
    boolean isDefaultValue() {
        return this.node.base().map(XmlDefaultValue.ADEFVALUE::equals).orElse(false);
    }
}
