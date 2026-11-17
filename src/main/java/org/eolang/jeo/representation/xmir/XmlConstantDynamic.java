/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.stream.Collectors;
import org.objectweb.asm.ConstantDynamic;

/**
 * XML representation of a dynamic constant.
 *
 * @since 0.18.0
 */
final class XmlConstantDynamic {

    /**
     * How many operands a constant dynamic carries.
     */
    private static final int OPERANDS = 4;

    /**
     * XML node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     *
     * @param xmlnode XML node
     */
    XmlConstantDynamic(final XmlNode xmlnode) {
        this.node = new XmlJeoObject(xmlnode);
    }

    /**
     * Convert to an ASM dynamic constant.
     *
     * @return Dynamic constant
     */
    ConstantDynamic constant() {
        final List<XmlNode> children = this.node.children()
            .collect(Collectors.toList());
        if (children.size() < XmlConstantDynamic.OPERANDS) {
            throw new IllegalStateException(
                String.format(
                    "Constant dynamic '%s' has %d operands, %d expected",
                    this.node,
                    children.size(),
                    XmlConstantDynamic.OPERANDS
                )
            );
        }
        return new ConstantDynamic(
            (String) new XmlOperand(children.get(0)).asObject(),
            (String) new XmlOperand(children.get(1)).asObject(),
            new XmlHandle(children.get(2)).bytecode().asHandle(),
            new XmlSeq(children.get(3)).children()
                .map(XmlOperand::new)
                .map(XmlOperand::asObject)
                .toArray()
        );
    }
}
