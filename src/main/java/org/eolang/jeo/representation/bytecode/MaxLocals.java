/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.Type;

/**
 * Bytecode method max locals.
 * This class knows hot to compute the maximum number of local variables
 * that can be used by a method.
 * The computation of max locals might give different results if computed only
 * by looking at bytecode instructions, because some of the variables we can see only
 * in the source code are not visible in the bytecode:
 * {@code
 * public void ia(){
 * long h$m$$;
 * boolean U$qa=true,A=U$qa==false,$z;
 * }
 * }
 * Here, the variable {@code $z} is not visible in the bytecode, because it is not used and it's
 * last variable in the method.
 *
 * @since 0.6
 */
final class MaxLocals {

    /**
     * Method properties.
     */
    private final BytecodeMethodProperties props;

    /**
     * Method instructions.
     */
    private final List<? extends BytecodeEntry> instructions;

    /**
     * Try-catch blocks.
     */
    private final List<BytecodeTryCatchBlock> blocks;

    /**
     * Constructor.
     *
     * @param props Method properties
     * @param instructions Instructions
     * @param blocks Try-catch blocks
     */
    MaxLocals(
        final BytecodeMethodProperties props,
        final List<? extends BytecodeEntry> instructions,
        final List<BytecodeTryCatchBlock> blocks
    ) {
        this.props = props;
        this.instructions = instructions;
        this.blocks = blocks;
    }

    /**
     * Compute the maximum number of local variables.
     *
     * @return Maximum number of local variables
     */
    int value() {
        return new InstructionsFlow<Variables>(this.instructions, this.blocks).max(
            this.initial(),
            instr -> {
                final Variables result;
                if (instr instanceof BytecodeInstruction
                    && ((BytecodeInstruction) instr).isVarInstruction()) {
                    result = new Variables((BytecodeInstruction) instr);
                } else {
                    result = new Variables();
                }
                return result;
            }
        ).orElse(new Variables()).size();
    }

    private Variables initial() {
        final List<Integer> init = new ArrayList<>(0);
        if (!this.props.isStatic()) {
            init.add(1);
        }
        Arrays.stream(Type.getArgumentTypes(this.props.descriptor()))
            .map(Type::getSize)
            .forEach(init::add);
        final Map<Integer, Integer> initial = new HashMap<>(0);
        int curr = 0;
        for (final Integer size : init) {
            initial.put(curr, size);
            curr += size;
        }
        return new Variables(initial);
    }
}
