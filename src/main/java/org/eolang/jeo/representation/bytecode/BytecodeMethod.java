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
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.xmir.AllLabels;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Bytecode method.
 * @since 0.1.0
 */
@ToString
@EqualsAndHashCode
public final class BytecodeMethod implements Testable {

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
     * Max stack and locals.
     */
    private final BytecodeMaxs maxs;

    /**
     * All Method Labels.
     */
    private final AllLabels labels;

    /**
     * Constructor.
     * @param name Method name.
     */
    public BytecodeMethod(final String name) {
        this(name, new BytecodeClass(), "()V", Opcodes.ACC_PUBLIC);
    }

    /**
     * Constructor.
     * @param name Method name.
     * @param clazz Original class.
     * @param descriptor Method descriptor.
     * @param modifiers Method modifiers.
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    BytecodeMethod(
        final String name,
        final BytecodeClass clazz,
        final String descriptor,
        final int... modifiers
    ) {
        this(new BytecodeMethodProperties(name, descriptor, "", modifiers), clazz);
    }

    /**
     * Constructor.
     * @param properties Method properties.
     * @param clazz Original class.
     */
    BytecodeMethod(
        final BytecodeMethodProperties properties,
        final BytecodeClass clazz
    ) {
        this(properties, clazz, new BytecodeMaxs(0, 0));
    }

    /**
     * Constructor.
     * @param properties Method properties.
     * @param clazz Original class.
     * @param maxs Max stack and locals.
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public BytecodeMethod(
        final BytecodeMethodProperties properties,
        final BytecodeClass clazz,
        final BytecodeMaxs maxs
    ) {
        this(
            clazz,
            new ArrayList<>(0),
            new ArrayList<>(0),
            new ArrayList<>(0),
            properties,
            new ArrayList<>(0),
            maxs
        );
    }

    /**
     * Constructor.
     * @param clazz Original class.
     * @param tryblocks Try-catch blocks.
     * @param instructions Method instructions.
     * @param annotations Method annotations.
     * @param properties Method properties.
     * @param defvalues Default values.
     * @param maxs Max stack and locals.
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public BytecodeMethod(
        final BytecodeClass clazz,
        final List<BytecodeEntry> tryblocks,
        final List<BytecodeEntry> instructions,
        final List<BytecodeAnnotation> annotations,
        final BytecodeMethodProperties properties,
        final List<BytecodeDefaultValue> defvalues,
        final BytecodeMaxs maxs
    ) {
        this.clazz = clazz;
        this.tryblocks = tryblocks;
        this.instructions = instructions;
        this.annotations = annotations;
        this.properties = properties;
        this.defvalues = defvalues;
        this.maxs = maxs;
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
        return this.entry(new BytecodeLabel(label, this.labels));
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
                "withMethod(%s)%n//maxs %s",
                this.properties.testCode(),
                this.maxs
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
    void write(final CustomClassWriter visitor) {
        try {
            final MethodVisitor mvisitor = this.properties.writeMethod(
                visitor,
                this.maxs.compute()
            );
            this.annotations.forEach(annotation -> annotation.write(mvisitor));
            this.defvalues.forEach(defvalue -> defvalue.writeTo(mvisitor));
            if (!this.properties.isAbstract()) {
                mvisitor.visitCode();
                this.tryblocks.forEach(block -> block.writeTo(mvisitor));
                this.instructions.forEach(instruction -> instruction.writeTo(mvisitor));
                mvisitor.visitMaxs(this.maxs.stack(), this.maxs.locals());
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
