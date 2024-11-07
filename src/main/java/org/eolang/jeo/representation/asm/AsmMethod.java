/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo.representation.asm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.eolang.jeo.representation.bytecode.BytecodeAttributes;
import org.eolang.jeo.representation.bytecode.BytecodeDefaultValue;
import org.eolang.jeo.representation.bytecode.BytecodeEntry;
import org.eolang.jeo.representation.bytecode.BytecodeMaxs;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.BytecodeMethodProperties;
import org.eolang.jeo.representation.bytecode.BytecodeTryCatchBlock;
import org.eolang.jeo.representation.bytecode.LocalVariable;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Asm method.
 * Asm parser for a method.
 * @since 0.6
 */
final class AsmMethod {

    /**
     * Method node.
     */
    private final MethodNode node;

    /**
     * Constructor.
     * @param node Method node.
     */
    AsmMethod(final MethodNode node) {
        this.node = node;
    }

    /**
     * Convert asm method to domain method.
     * @return Domain method.
     */
    BytecodeMethod bytecode() {
        return new BytecodeMethod(
            this.tryblocks(),
            this.instructions(),
            new AsmAnnotations(this.node).annotations(),
            new BytecodeMethodProperties(
                this.node.access,
                this.node.name,
                this.node.desc,
                this.node.signature,
                new AsmMethodParameters(this.node).bytecode(),
                this.node.exceptions.toArray(new String[0])
            ),
            this.defvalue(),
            this.maxs(),
            this.attributes()
        );
    }

    /**
     * Convert asm method to domain method attributes.
     * @return Domain method attributes.
     */
    private BytecodeAttributes attributes() {
        final List<LocalVariableNode> variables = this.node.localVariables;
        final BytecodeAttributes result;
        if (variables == null) {
            result = new BytecodeAttributes();
        } else {
            result = new BytecodeAttributes(
                variables.stream()
                    .map(LocalVariable::new)
                    .toArray(BytecodeAttribute[]::new)
            );
        }
        return result;
    }


    /**
     * Convert asm method to domain method maxs.
     * @return Domain method maxs.
     */
    private BytecodeMaxs maxs() {
        return new BytecodeMaxs(this.node.maxStack, this.node.maxLocals);
    }

    /**
     * Convert asm method to domain method tryblocks.
     * @return Domain method tryblocks.
     */
    private List<BytecodeEntry> tryblocks() {
        return this.node.tryCatchBlocks.stream()
            .map(
                block -> new BytecodeTryCatchBlock(
                    block.start.getLabel(),
                    block.end.getLabel(),
                    block.handler.getLabel(),
                    block.type
                )
            )
            .collect(Collectors.toList());
    }

    /**
     * Convert asm class to domain class.
     * @return Domain class.
     */
    private List<BytecodeEntry> instructions() {
        return Arrays.stream(this.node.instructions.toArray())
            .map(AsmInstruction::new)
            .map(AsmInstruction::bytecode)
            .collect(Collectors.toList());
    }

    /**
     * Convert asm default value to domain default value.
     * @return Domain default value.
     */
    private List<BytecodeDefaultValue> defvalue() {
        final List<BytecodeDefaultValue> result;
        if (this.node.annotationDefault == null) {
            result = Collections.emptyList();
        } else {
            result = Collections.singletonList(
                new BytecodeDefaultValue(
                    new AsmAnnotationProperty(this.node.annotationDefault).bytecode()
                )
            );
        }
        return result;
    }
}
