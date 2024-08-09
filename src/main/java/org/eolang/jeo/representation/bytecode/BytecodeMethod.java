/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.List;
import org.eolang.jeo.representation.xmir.AllLabels;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * Bytecode method.
 * @since 0.1.0
 */
public final class BytecodeMethod implements Testable {

    /**
     * ASM class visitor.
     */
    private final CustomClassWriter visitor;

    /**
     * Original class.
     */
    private final BytecodeClass clazz;

    /**
     * Try-catch blocks.
     */
    private final List<BytecodeEntry> tryblocks;

    /**
     * Method Instructions.
     */
    private final List<BytecodeEntry> instructions;

    /**
     * Method annotations.
     */
    private final List<BytecodeAnnotation> annotations;

    /**
     * Method properties.
     */
    private final BytecodeMethodProperties properties;

    /**
     * Default value.
     */
    private final List<BytecodeDefaultValue> defvalues;

    /**
     * Stack size.
     */
    private final int stack;

    /**
     * Local variables.
     */
    private final int locals;

    /**
     * All Method Labels.
     */
    private final AllLabels labels;

    /**
     * Constructor.
     * @param name Method name.
     * @param visitor ASM class writer.
     * @param clazz Original class.
     * @param descriptor Method descriptor.
     * @param modifiers Method modifiers.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    BytecodeMethod(
        final String name,
        final CustomClassWriter visitor,
        final BytecodeClass clazz,
        final String descriptor,
        final int... modifiers
    ) {
        this(new BytecodeMethodProperties(name, descriptor, modifiers), visitor, clazz);
    }

    /**
     * Constructor.
     * @param properties Method properties.
     * @param visitor ASM class writer.
     * @param clazz Original class.
     */
    BytecodeMethod(
        final BytecodeMethodProperties properties,
        final CustomClassWriter visitor,
        final BytecodeClass clazz
    ) {
        this(properties, visitor, clazz, 0, 0);
    }

    /**
     * Constructor.
     * @param properties Method properties.
     * @param visitor ASM class writer.
     * @param clazz Original class.
     * @param stack Stack size.
     * @param locals Local variables.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public BytecodeMethod(
        final BytecodeMethodProperties properties,
        final CustomClassWriter visitor,
        final BytecodeClass clazz,
        final int stack,
        final int locals
    ) {
        this.properties = properties;
        this.visitor = visitor;
        this.clazz = clazz;
        this.tryblocks = new ArrayList<>(0);
        this.instructions = new ArrayList<>(0);
        this.annotations = new ArrayList<>(0);
        this.defvalues = new ArrayList<>(0);
        this.stack = stack;
        this.locals = locals;
        this.labels = new AllLabels();
    }

    /**
     * Return to the original class.
     * @return Original class.
     * @checkstyle MethodNameCheck (3 lines)
     */
    @SuppressWarnings("PMD.ShortMethodName")
    public BytecodeClass up() {
        return this.clazz;
    }

    public BytecodeMethod label(final String uid) {
        return this.label(this.labels.label(uid));
    }

    /**
     * Add label.
     * @param label Label.
     * @return This object.
     */
    public BytecodeMethod label(final Label label) {
        return this.entry(new BytecodeLabelEntry(label, this.labels));
    }

    /**
     * Add instruction.
     * @param opcode Opcode.
     * @param args Arguments.
     * @return This object.
     */
    public BytecodeMethod opcode(final int opcode, final Object... args) {
        return this.entry(new BytecodeInstructionEntry(this.labels, opcode, args));
    }

    /**
     * Add try-catch block.
     * @param entry Try-catch block.
     * @return This object.
     */
    public BytecodeMethod trycatch(final BytecodeEntry entry) {
        this.tryblocks.add(entry);
        return this;
    }

    /**
     * Add some bytecode entry.
     * @param entry Entry.
     * @return This object.
     */
    public BytecodeMethod entry(final BytecodeEntry entry) {
        this.instructions.add(entry);
        return this;
    }

    /**
     * Add annotation.
     * @param annotation Annotation.
     * @return This object.
     */
    public BytecodeMethod annotation(final BytecodeAnnotation annotation) {
        this.annotations.add(annotation);
        return this;
    }

    /**
     * Add default value.
     * @param defvalue Default value.
     * @return This object.
     */
    public BytecodeMethod defvalue(final BytecodeDefaultValue defvalue) {
        this.defvalues.add(defvalue);
        return this;
    }

    @Override
    @SuppressWarnings("PMD.InsufficientStringBufferDeclaration")
    public String testCode() {
        final StringBuilder res = new StringBuilder(
            String.format(
                "withMethod(%s)%n//max stack: %d max locals %d%n",
                this.properties.testCode(),
                this.stack,
                this.locals
            )
        );
        for (final BytecodeEntry instruction : this.instructions) {
            res.append(instruction.testCode()).append('\n');
        }
        res.append(".up()");
        return res.toString();
    }

    /**
     * Generate bytecode.
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    void write() {
        try {
            final MethodVisitor mvisitor = this.properties.writeMethod(
                this.visitor,
                this.stack == 0 && this.locals == 0
            );
            this.annotations.forEach(annotation -> annotation.write(mvisitor));
            this.defvalues.forEach(defvalue -> defvalue.writeTo(mvisitor));
            if (!this.properties.isAbstract()) {
                mvisitor.visitCode();
                this.tryblocks.forEach(block -> block.writeTo(mvisitor));
                this.instructions.forEach(instruction -> instruction.writeTo(mvisitor));
                mvisitor.visitMaxs(this.stack, this.locals);
            }
            mvisitor.visitEnd();
        } catch (final NegativeArraySizeException exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to write method %s",
                    this.properties
                ),
                exception
            );
        } catch (final UnrecognizedOpcode exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to write method %s, because some of the opcodes are not recognized",
                    this.properties
                ),
                exception
            );
            // @checkstyle IllegalCatchCheck (1 line)
        } catch (final ClassFormatError format) {
            throw new IllegalStateException(
                String.format(
                    "Failed to generate bytecode method %s in class %s due to class format error,",
                    this.properties,
                    this.clazz
                ),
                format
            );
            // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Exception exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to generate bytecode method %s due to unexpected exception",
                    this.properties
                ),
                exception
            );
        }
    }
}
