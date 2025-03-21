/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.HashMap;
import java.util.Map;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.objectweb.asm.Label;

/**
 * Asm Method Labels.
 * Used during method generation to keep track of all the labels.
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
     * @param labels All the labels.
     */
    public AsmLabels(final Map<String, Label> labels) {
        this.labels = labels;
    }

    /**
     * Get label by UID.
     * @param label Bytecode label.
     * @return Label.
     */
    public Label label(final BytecodeLabel label) {
        return this.labels.computeIfAbsent(label.uid(), id -> new Label());
    }
}
