/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameter;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameters;
import org.eolang.jeo.representation.bytecode.BytecodeParamAnnotations;
import org.eolang.jeo.representation.directives.JeoFqn;

/**
 * XML method params.
 * @since 0.6
 */
final class XmlMethodParams {

    /**
     * Params fully qualified name.
     */
    private static final String PARAMS_BASE = new JeoFqn("params").fqn();

    /**
     * Xml representation of a method params.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param node Xml representation of a method params.
     */
    XmlMethodParams(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * Constructor.
     * @param node XML Jeo object node representing the method params.
     */
    private XmlMethodParams(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Is this node a method params?
     * @return True if this node is a method params.
     */
    boolean isParams() {
        return this.node.base()
            .map(XmlMethodParams.PARAMS_BASE::equals)
            .orElse(false);
    }

    /**
     * Get method params.
     * @return Method params.
     */
    BytecodeMethodParameters params() {
        return new BytecodeMethodParameters(
            this.parameters(),
            this.annotations()
        );
    }

    /**
     * Method parameters.
     * @return List of bytecode method parameters.
     */
    private List<BytecodeMethodParameter> parameters() {
        return this.node.children()
            .map(XmlMethodParam::new)
            .filter(XmlMethodParam::isParam)
            .map(XmlMethodParam::bytecode)
            .collect(Collectors.toList());
    }

    /**
     * Annotations of the parameter.
     * @return Annotations.
     */
    private List<BytecodeParamAnnotations> annotations() {
        return this.node.children()
            .map(XmlJeoObject::new)
            .map(XmlParamAnnotations::new)
            .filter(XmlParamAnnotations::isParamAnnotations)
            .map(XmlParamAnnotations::bytecode)
            .collect(Collectors.toList());
    }
}
