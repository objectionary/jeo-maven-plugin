/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.MethodName;
import org.eolang.jeo.representation.NumberedName;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.DirectivesMethod;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Bytecode method.
 * @since 0.1.0
 */
@ToString
@EqualsAndHashCode
@SuppressWarnings("PMD.TooManyMethods")
public final class BytecodeMethod {

    /**
     * Try-catch blocks.
     */
    private final List<BytecodeEntry> tryblocks;

    /**
     * Method Instructions.
     */
    private final List<BytecodeEntry> entries;

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
     * Bytecode method attributes.
     */
    private final BytecodeAttributes attributes;

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
    public BytecodeMethod(final BytecodeEntry... instructions) {
        this(
            new ArrayList<>(0),
            Arrays.asList(instructions),
            new BytecodeAnnotations(),
            new BytecodeMethodProperties("foo", "()V", "", Opcodes.ACC_PUBLIC),
            new ArrayList<>(0),
            new BytecodeMaxs(),
            new BytecodeAttributes()
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
            new BytecodeMaxs(),
            new BytecodeAttributes()
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
        this(properties, new BytecodeMaxs());
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
            maxs,
            new BytecodeAttributes()
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
     * @param attributes Method attributes.
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public BytecodeMethod(
        final List<BytecodeEntry> tryblocks,
        final List<BytecodeEntry> instructions,
        final BytecodeAnnotations annotations,
        final BytecodeMethodProperties properties,
        final List<BytecodeDefaultValue> defvalues,
        final BytecodeMaxs maxs,
        final BytecodeAttributes attributes
    ) {
        this.tryblocks = tryblocks;
        this.entries = instructions;
        this.annotations = annotations;
        this.properties = properties;
        this.defvalues = defvalues;
        this.maxs = maxs;
        this.attributes = attributes;
    }

    /**
     * Similar method without maxs.
     * @return Method without maxs.
     */
    public BytecodeMethod withoutMaxs() {
        return new BytecodeMethod(
            this.tryblocks,
            this.entries,
            this.annotations,
            this.properties,
            this.defvalues,
            new BytecodeMaxs(),
            this.attributes
        );
    }

    /**
     * Add label.
     * @param label Label.
     * @return This object.
     */
    public BytecodeMethod label(final String label) {
        return this.entry(new BytecodeLabel(label));
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
        this.entries.add(entry);
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

    /**
     * Method instructions.
     * @return Instructions.
     */
    public List<BytecodeEntry> instructions() {
        return Collections.unmodifiableList(this.entries);
    }

    /**
     * Generate directives.
     * Since EO can't have overloaded methods, we need to add suffix to their names.
     * This suffix is a number of the method.
     * For example, if we have two methods with the same name, say 'foo',
     * then we add suffixes to their names:
     * foo and foo-2.
     * That is why we need to pass method number to this method.
     * @param number Method number.
     * @return Directives.
     */
    public DirectivesMethod directives(final int number) {
        return new DirectivesMethod(
            new NumberedName(
                number,
                new MethodName(this.properties.name()).xmir()
            ),
            this.properties.directives(this.maxs),
            this.entries.stream().map(BytecodeEntry::directives)
                .collect(Collectors.toList()),
            this.tryblocks.stream().map(BytecodeEntry::directives)
                .collect(Collectors.toList()),
            this.annotations.directives(),
            this.defvalues.stream()
                .map(BytecodeDefaultValue::directives)
                .collect(Collectors.toList()),
            this.attributes.directives("local-variable-table")
        );
    }

    /**
     * Generate directives.
     * @return Directives.
     */
    DirectivesMethod directives() {
        return this.directives(1);
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
            final AsmLabels all = new AsmLabels();
            if (!this.properties.isAbstract()) {
                mvisitor.visitCode();
                this.tryblocks.forEach(block -> block.writeTo(mvisitor, all));
                this.entries.forEach(instruction -> instruction.writeTo(mvisitor, all));
                final BytecodeMaxs max;
                if (this.maxs.compute()) {
                    max = this.computeMaxs();
                } else {
                    max = this.maxs;
                }
                mvisitor.visitMaxs(max.stack(), max.locals());
            }
            this.attributes.write(mvisitor, all);
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
     * Compute frames.
     * @return Frames.
     */
    List<BytecodeFrame> computeFrames() {
        return new StackMapFrames(
            this.properties,
            this.instructions,
            this.tryblocks.stream()
                .filter(BytecodeTryCatchBlock.class::isInstance)
                .map(BytecodeTryCatchBlock.class::cast)
                .collect(Collectors.toList())
        ).frames();
    }

    /**
     * Current frames.
     * @return Frames.
     */
    List<BytecodeFrame> currentFrames() {
        return this.instructions.stream()
            .filter(BytecodeFrame.class::isInstance)
            .map(BytecodeFrame.class::cast)
            .collect(Collectors.toList());
    }

    /**
     * Add instruction.
     * @param opcode Opcode.
     * @param args Arguments.
     * @return This object.
     */
    BytecodeMethod opcode(final int opcode, final Object... args) {
        return this.entry(new BytecodeInstruction(opcode, args));
    }

    /**
     * Prints instructions in human-readable format.
     * @return Instructions view in human-readable format.
     */
    String instructionsView() {
        return this.entries.stream()
            .map(BytecodeEntry::view)
            .collect(Collectors.joining("\n"));
    }

    /**
     * Compute max stack.
     * @return Max stack.
     */
    private int computeStack() {
        return new MaxStack(
            this.entries,
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
            this.entries,
            this.tryblocks.stream()
                .filter(BytecodeTryCatchBlock.class::isInstance)
                .map(BytecodeTryCatchBlock.class::cast)
                .collect(Collectors.toList())
        ).value();
    }
}
