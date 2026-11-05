/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Optional;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameter;
import org.eolang.jeo.representation.directives.JeoFqn;
import org.objectweb.asm.Type;

/**
 * Xmir representation of a method parameter.
 *
 * @since 0.4
 */
public final class XmlMethodParam {

    /**
     * Root node from which we will get all required data.
     */
    private final XmlJeoObject root;

    /**
     * Constructor.
     *
     * @param root Parameter xml node
     */
    XmlMethodParam(final XmlNode root) {
        this(new XmlJeoObject(root));
    }

    /**
     * Constructor.
     *
     * @param root Parameter xml node
     */
    private XmlMethodParam(final XmlJeoObject root) {
        this.root = root;
    }

    /**
     * Convert to bytecode.
     *
     * @return Bytecode method parameter
     */
    public BytecodeMethodParameter bytecode() {
        return new BytecodeMethodParameter(
            this.index(),
            this.name(),
            this.access(),
            this.type()
        );
    }

    /**
     * Check if the object is actually a method param.
     *
     * @return True if the method param, false otherwise
     */
    public boolean isParam() {
        return this.root.base().map(new JeoFqn("param").fqn()::equals).orElse(false);
    }

    private Type type() {
        return Type.getType(this.child("type").string());
    }

    private String name() {
        return this.ochild("name").map(XmlValue::string).orElse(null);
    }

    private int access() {
        return (int) this.child("access").object();
    }

    private int index() {
        return (int) this.child("index").object();
    }

    private XmlValue child(final String name) {
        return this.ochild(name).orElseThrow(
            () -> new IllegalStateException(
                String.format(
                    "Child with attribute 'name'='%s' not found in node '%s'", name,
                    this.root
                )
            )
        );
    }

    private Optional<XmlValue> ochild(final String name) {
        return this.root.children()
            .filter(node -> XmlMethodParam.hasName(node, name))
            .findFirst()
            .map(XmlValue::new);
    }

    private static boolean hasName(final XmlNode node, final String name) {
        return node.attribute("name").map(name::equals).orElse(false);
    }
}
