/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.objectweb.asm.Label;

/**
 * Asm Method Labels.
 * Used during method generation to keep track of all the labels.
 *
 * @since 0.6
 */
public final class AsmLabels {

    /**
     * All the method labels.
     */
    private final Map<String, Label> labels;

    /**
     * Constructor.
     */
    public AsmLabels() {
        this(new HashMap<>(0));
    }

    /**
     * Constructor.
     *
     * @param labels All the labels
     */
    public AsmLabels(final Map<String, Label> labels) {
        this.labels = labels;
    }

    /**
     * Get label by UID.
     *
     * @param label Bytecode label
     * @return Label
     */
    public Label label(final BytecodeLabel label) {
        return this.labels.computeIfAbsent(label.uid(), id -> new Label());
    }

    /**
     * Identifiers of the labels that were referenced, but never placed in the method.
     *
     * @return Identifiers of the labels
     */
    public List<String> unresolved() {
        return this.labels.entrySet().stream()
            .filter(
                entry -> {
                    boolean placed;
                    try {
                        entry.getValue().getOffset();
                        placed = true;
                    } catch (final IllegalStateException ignored) {
                        placed = false;
                    }
                    return !placed;
                }
            )
            .map(Map.Entry::getKey)
            .sorted()
            .collect(Collectors.toList());
    }
}
