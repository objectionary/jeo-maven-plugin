/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.objectweb.asm.Opcodes;

/**
 * Opcode name.
 * The name of bytecode instruction. The name combined with unique number in order to
 * avoid name collisions.
 * @since 0.1.0
 */
public final class OpcodeName {

    /**
     * Opcode names.
     */
    private static final Map<Integer, String> NAMES = OpcodeName.init();

    /**
     * Default counter.
     */
    private static final AtomicInteger DEFAULT = new AtomicInteger(0);

    /**
     * Unknown opcode name.
     */
    private static final String UNKNOWN = "unknown";

    /**
     * Bytecode operation code.
     */
    private final int opcode;

    /**
     * Opcode counter.
     */
    private final AtomicInteger counter;

    /**
     * Constructor.
     * @param opcode Bytecode operation code.
     */
    public OpcodeName(final int opcode) {
        this(opcode, OpcodeName.DEFAULT);
    }

    /**
     * Constructor.
     * @param opcode Bytecode operation code.
     * @param counter Opcode counter.
     */
    OpcodeName(final int opcode, final AtomicInteger counter) {
        this.opcode = opcode;
        this.counter = counter;
    }

    /**
     * Get simplified opcode name without counter.
     * @return Simplified opcode name.
     */
    public String simplified() {
        return OpcodeName.NAMES.getOrDefault(this.opcode, OpcodeName.UNKNOWN);
    }

    /**
     * Opcode.
     * @return Opcode
     * @todo #770:35min Get rid of code() method.
     *  This method looks like a getter. For now this method should be used by
     *  <a href="https://github.com/objectionary/opeo-maven-plugin">opeo</a>
     *  in it/AgentsIT.java. Let's remove code() method, so this class will be
     *  getter-free.
     */
    public int code() {
        return this.opcode;
    }

    /**
     * Get string representation of a bytecode.
     * @return String representation of a bytecode.
     */
    public String asString() {
        final String opc = OpcodeName.NAMES.getOrDefault(this.opcode, OpcodeName.UNKNOWN);
        return String.format("%s-%X", opc, this.counter.incrementAndGet());
    }

    /**
     * Initialize opcode names.
     * @return Opcode names.
     */
    private static Map<Integer, String> init() {
        try {
            final Map<Integer, String> res = new HashMap<>();
            for (final Field field : Opcodes.class.getFields()) {
                if (field.getType() == int.class) {
                    res.put(field.getInt(Opcodes.class), field.getName().toLowerCase(Locale.ROOT));
                }
            }
            return res;
        } catch (final IllegalAccessException exception) {
            throw new IllegalStateException(
                String.format("Can't retrieve opcode names from '%s'", Opcodes.class),
                exception
            );
        }
    }
}
