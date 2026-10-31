/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LabelNode;

/**
 * Identifiers of the labels of one method.
 *
 * <p>A label gets the next number the first time it is seen, so the identifiers
 * are unique within the method and the same every time the method is read.
 * {@link Label#toString()} is not used, since it is an identity hash code,
 * which two labels may share and which differs from run to run.</p>
 *
 * @since 0.18.0
 */
final class AsmLabelIds {

    /**
     * Identifiers of the labels seen so far.
     */
    private final Map<Label, String> ids;

    /**
     * Constructor.
     */
    AsmLabelIds() {
        this(new IdentityHashMap<>(0));
    }

    /**
     * Constructor.
     *
     * @param ids Identifiers of the labels seen so far
     */
    private AsmLabelIds(final Map<Label, String> ids) {
        this.ids = ids;
    }

    /**
     * The label of the node.
     *
     * @param node Label node
     * @return Bytecode label with the identifier of the node
     */
    BytecodeLabel label(final LabelNode node) {
        return new BytecodeLabel(
            this.ids.computeIfAbsent(
                node.getLabel(), label -> String.format("L%d", this.ids.size())
            )
        );
    }

    /**
     * Items of a frame, with every label node replaced by its label.
     *
     * @param items Items of a frame, may be absent
     * @return Items with labels
     */
    List<Object> labeled(final List<Object> items) {
        return Optional.ofNullable(items).orElse(Collections.emptyList()).stream().map(
            item -> {
                final Object result;
                if (item instanceof LabelNode) {
                    result = this.label((LabelNode) item);
                } else {
                    result = item;
                }
                return result;
            }
        ).collect(Collectors.toList());
    }
}
