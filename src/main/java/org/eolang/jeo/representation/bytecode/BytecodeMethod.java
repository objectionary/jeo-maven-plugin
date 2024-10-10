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
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.MethodName;
import org.eolang.jeo.representation.Signature;
import org.eolang.jeo.representation.directives.DirectivesMethod;
import org.eolang.jeo.representation.xmir.AllLabels;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Bytecode method.
 * @since 0.1.0
 */
@ToString
@EqualsAndHashCode
@SuppressWarnings("PMD.TooManyMethods")
public final class BytecodeMethod implements Testable {

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
    private final BytecodeAnnotations annotations;

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
     * Constructor for tests.
     */
    public BytecodeMethod() {
        this("foo");
    }

    /**
     * Constructor.
     * @param name Method name.
     */
    public BytecodeMethod(final String name) {
        this(name, "()V", Opcodes.ACC_PUBLIC);
    }

    /**
     * Constructor.
     * @param instructions Method instructions.
     */
    public BytecodeMethod(BytecodeEntry... instructions) {
        this(
            new ArrayList<>(0),
            Arrays.asList(instructions),
            new BytecodeAnnotations(),
            new BytecodeMethodProperties("foo", "()V", "", Opcodes.ACC_PUBLIC),
            new ArrayList<>(0),
            new BytecodeMaxs(0, 0)
        );
    }

    /**
     * Constructor.
     * @param name Method name.
     * @param annotations Method annotations.
     */
    public BytecodeMethod(final String name, final BytecodeAnnotations annotations) {
        this(
            new ArrayList<>(0),
            new ArrayList<>(0),
            annotations,
            new BytecodeMethodProperties(name, "()V", "", Opcodes.ACC_PUBLIC),
            new ArrayList<>(0),
            new BytecodeMaxs(0, 0)
        );
    }

    /**
     * Constructor.
     * @param name Method name.
     * @param maxs Max stack and locals.
     */
    public BytecodeMethod(final String name, final BytecodeMaxs maxs) {
        this(new BytecodeMethodProperties(name, "()V", "", 1), maxs);
    }

    /**
     * Constructor.
     * @param name Method name.
     * @param descriptor Method descriptor.
     * @param modifiers Method modifiers.
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    BytecodeMethod(final String name, final String descriptor, final int... modifiers) {
        this(new BytecodeMethodProperties(name, descriptor, "", modifiers));
    }

    /**
     * Constructor.
     * @param properties Method properties.
     */
    BytecodeMethod(final BytecodeMethodProperties properties) {
        this(properties, new BytecodeMaxs(0, 0));
    }

    /**
     * Constructor.
     * @param properties Method properties.
     * @param maxs Max stack and locals.
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public BytecodeMethod(
        final BytecodeMethodProperties properties,
        final BytecodeMaxs maxs
    ) {
        this(
            new ArrayList<>(0),
            new ArrayList<>(0),
            new BytecodeAnnotations(),
            properties,
            new ArrayList<>(0),
            maxs
        );
    }

    /**
     * Constructor.
     * @param tryblocks Try-catch blocks.
     * @param instructions Method instructions.
     * @param annotations Method annotations.
     * @param properties Method properties.
     * @param defvalues Default values.
     * @param maxs Max stack and locals.
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public BytecodeMethod(
        final List<BytecodeEntry> tryblocks,
        final List<BytecodeEntry> instructions,
        final BytecodeAnnotations annotations,
        final BytecodeMethodProperties properties,
        final List<BytecodeDefaultValue> defvalues,
        final BytecodeMaxs maxs
    ) {
        this.tryblocks = tryblocks;
        this.instructions = instructions;
        this.annotations = annotations;
        this.properties = properties;
        this.defvalues = defvalues;
        this.maxs = maxs;
        this.labels = new AllLabels();
    }

    /**
     * Similar method without maxs.
     * @return Method without maxs.
     */
    public BytecodeMethod withoutMaxs() {
        return new BytecodeMethod(
            this.tryblocks,
            this.instructions,
            this.annotations,
            this.properties,
            this.defvalues,
            new BytecodeMaxs()
        );
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
     * Add default value.
     * @param defvalue Default value.
     * @return This object.
     */
    public BytecodeMethod defvalue(final BytecodeDefaultValue defvalue) {
        this.defvalues.add(defvalue);
        return this;
    }

    /**
     * Method name.
     * @return Method name.
     */
    public String name() {
        return this.properties.name();
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
     * Generate directives.
     * @param counting Whether to count opcodes.
     * @return Directives.
     */
    public DirectivesMethod directives(final boolean counting) {
        return new DirectivesMethod(
            new Signature(
                new MethodName(this.properties.name()).xmir(), this.properties.descriptor()
            ),
            this.properties.directives(this.maxs),
            this.instructions.stream().map(entry -> entry.directives(counting))
                .collect(Collectors.toList()),
            this.tryblocks.stream().map(entry -> entry.directives(counting))
                .collect(Collectors.toList()),
            this.annotations.directives(),
            this.defvalues.stream()
                .map(BytecodeDefaultValue::directives)
                .collect(Collectors.toList()),
            counting
        );
    }

    /**
     * Generate bytecode.
     * @param visitor Visitor.
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    void write(final CustomClassWriter visitor) {
        try {
            final MethodVisitor mvisitor = this.properties.writeMethod(
                visitor,
                this.maxs.compute()
            );
            this.annotations.write(mvisitor);
            this.defvalues.forEach(defvalue -> defvalue.writeTo(mvisitor));
            if (!this.properties.isAbstract()) {
                mvisitor.visitCode();
                this.tryblocks.forEach(block -> block.writeTo(mvisitor));
                this.instructions.forEach(instruction -> instruction.writeTo(mvisitor));
                mvisitor.visitMaxs(this.computeStack(), this.computeLocals());
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
                    "Failed to generate bytecode method %s due to class format error,",
                    this.properties
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

    /**
     * Compute maxs.
     * @return Maxs.
     */
    BytecodeMaxs computeMaxs() {
        return new BytecodeMaxs(this.computeStack(), this.computeLocals());
    }

    /**
     * Current maxs.
     * @return Maxs.
     */
    BytecodeMaxs currentMaxs() {
        return this.maxs;
    }

    /**
     * Add instruction.
     * @param opcode Opcode.
     * @param args Arguments.
     * @return This object.
     */
    BytecodeMethod opcode(final int opcode, final Object... args) {
        return this.entry(new BytecodeInstruction(this.labels, opcode, args));
    }

    /**
     * Add label.
     * @param uid Label uid.
     * @return This object.
     */
    BytecodeMethod label(final String uid) {
        return this.label(this.labels.label(uid));
    }

    /**
     * Whether the method has opcodes.
     * @return True if method has opcodes.
     */
    boolean hasOpcodes() {
        return this.instructions.stream().anyMatch(BytecodeEntry::isOpcode);
    }

    /**
     * Whether the method has labels.
     * @return True if method has labels.
     */
    boolean hasLabels() {
        return this.instructions.stream().anyMatch(BytecodeEntry::isLabel);
    }

    /**
     * Convert to directives with opcodes' counting.
     * @return Directives.
     */
    DirectivesMethod directives() {
        return this.directives(true);
    }

    /**
     * Compute max stack.
     * @return Max stack.
     */
    private int computeStack() {
        return new MaxStack(
            this.instructions,
            this.tryblocks.stream()
                .filter(BytecodeTryCatchBlock.class::isInstance)
                .map(BytecodeTryCatchBlock.class::cast)
                .collect(Collectors.toList())
        ).value();
    }

    /**
     * Compute max local variables.
     * @return Max local variables.
     */
    private int computeLocals() {
        return new MaxLocals(
            this.properties,
            this.instructions,
            this.tryblocks.stream()
                .filter(BytecodeTryCatchBlock.class::isInstance)
                .map(BytecodeTryCatchBlock.class::cast)
                .collect(Collectors.toList())
        ).value();
    }


}
