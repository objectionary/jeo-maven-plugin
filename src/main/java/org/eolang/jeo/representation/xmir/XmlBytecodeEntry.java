/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeEntry;

/**
 * XML representation of bytecode instruction or a label.
 * Usually each method in bytecode contains a list of bytecode entries.
 * Since they aren't always instructions, we call them entries.
 * @since 0.1
 */
public interface XmlBytecodeEntry {

    /**
     * Convert to bytecode entry.
     * @return Bytecode entry.
     */
    BytecodeEntry bytecode();
}
