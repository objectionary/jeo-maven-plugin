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

import com.jcabi.log.Logger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
import org.objectweb.asm.Type;

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
//                this.maxs.compute()
                false
            );
            this.annotations.write(mvisitor);
            this.defvalues.forEach(defvalue -> defvalue.writeTo(mvisitor));
            if (!this.properties.isAbstract()) {
                mvisitor.visitCode();
                this.tryblocks.forEach(block -> block.writeTo(mvisitor));
                this.instructions.forEach(instruction -> instruction.writeTo(mvisitor));
//                mvisitor.visitMaxs(this.maxs.stack(), this.maxs.locals());
                final int stack = this.computeStack();
//                final int locals = this.computeLocals();
//                final int locals = this.computeLocalsWithCFG();
                final int locals = this.computeLocalsWithCFG2();
                mvisitor.visitMaxs(stack, locals);
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
        return new BytecodeMaxs(this.computeStack(), this.computeLocalsWithCFG2());
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

    private int computeStack() {
        Logger.info(this, "Computing stack for %s", this.properties);
        int max = 0;
        final Deque<Integer> worklist = new ArrayDeque<>(0);
        final int length = this.instructions.size();
        worklist.add(0);
        Map<Integer, Integer> visited = new TreeMap<>();
        this.tryblocks.stream()
            .map(BytecodeTryCatchBlock.class::cast)
            .map(BytecodeTryCatchBlock::handler)
            .map(this::index)
            .peek(ind -> visited.put(ind, 1))
            .forEach(worklist::add);
        while (!worklist.isEmpty()) {
            int current = worklist.pop();
            int stack = visited.get(current) == null ? 0 : visited.get(current);
            while (current < length) {
                BytecodeEntry entry = this.instructions.get(current);
                stack += entry.stackImpact();
                max = Math.max(max, stack);
                final int finalStack = stack;
                visited.compute(
                    current, (k, v) -> v == null ? finalStack : Math.max(v, finalStack)
                );
                if (entry instanceof BytecodeInstruction) {
                    final BytecodeInstruction var = BytecodeInstruction.class.cast(entry);
                    if (var.isBranchInstruction()) {
                        if (var.isSwitchInstruction()) {
                            final List<Label> offsets = var.offsets();
                            for (Label offset : offsets) {
                                final int target = this.index(offset);
                                if (visited.get(target) == null
                                    || visited.get(target) < stack
                                ) {
                                    visited.put(target, stack);
                                    worklist.add(target);
                                }
                            }
                            break;
                        } else if (var.isConditionalBranchInstruction()) {
                            final int jump = this.index(var.offset());
                            if (visited.get(jump) == null
                                || visited.get(jump) < stack
                            ) {
                                visited.put(jump, stack);
                                worklist.add(jump);

                            }
                            final int next = current + 1;
                            if (visited.get(next) == null
                                || visited.get(next) < stack
                            ) {
                                visited.put(next, stack);
                                worklist.add(next);
                            }
                            break;
                        } else if (var.isReturnInstruction()) {
                            break;
                        } else {
                            final int jump = this.index(var.offset());
                            if (visited.get(jump) == null
                                || visited.get(jump) < stack
                            ) {
                                visited.put(jump, stack);
                                worklist.add(jump);
                            }
                            break;
                        }
                    }
                }
                current++;
            }
        }
        return max;
    }

    private String trackStack(final Map<Integer, Integer> visited) {
        String res = "";
        for (int i = 0; i < this.instructions.size(); i++) {
            res += String.format("%s: %d\n", this.instructions.get(i).testCode(), visited.get(i));
        }
        return res;
    }

    private int index(final Label label) {
        for (int index = 0; index < this.instructions.size(); index++) {
            final BytecodeEntry entry = this.instructions.get(index);
            final BytecodeLabel obj = new BytecodeLabel(label, new AllLabels());
            final boolean equals = entry.equals(obj);
            if (equals) {
                return index;
            }
        }
        throw new IllegalStateException("Label not found");
    }

    /**
     * Compute max local variables.
     * @return Max local variables.
     */
    private int computeLocals() {
        Map<Integer, Integer> variables = new HashMap<>(0);
        int first = 0;
        if (!this.properties.isStatic()) {
            variables.put(0, 1);
            first = 1;
        }
        final Type[] types = Type.getArgumentTypes(this.properties.descriptor());
        for (int index = 0; index < types.length; index++) {
            final Type type = types[index];
            variables.put(index * type.getSize() + first, type.getSize());
        }
        for (final BytecodeEntry instruction : this.instructions) {
            if (instruction instanceof BytecodeInstruction) {
                final BytecodeInstruction var = BytecodeInstruction.class.cast(instruction);
                if (var.isVarInstruction()) {
                    if (var.size() == 2) {
                        final int key = var.localIndex();
                        variables.put(key, 2);
                    } else {
                        final int key = var.localIndex();
                        variables.putIfAbsent(key, 1);
                        variables.compute(key, (k, v) -> v == 2 ? 2 : 1);
                    }
                }
            }
        }
        int max = variables.values().stream().mapToInt(Integer::intValue).sum();
        return max;
    }


    private int computeLocalsWithCFG() {
        Logger.info(this, "Computing locals for %s", this.properties);
        Map<Integer, Integer> variables = new HashMap<>(0);
        int first = 0;
        if (!this.properties.isStatic()) {
            variables.put(0, 1);
            first = 1;
        }
        final Type[] types = Type.getArgumentTypes(this.properties.descriptor());
        for (int index = 0; index < types.length; index++) {
            final Type type = types[index];
            variables.put(index * type.getSize() + first, type.getSize());
        }
        Map<Integer, Map<Integer, Integer>> allVariables = new HashMap<>(1);
        allVariables.put(0, variables);
        Deque<Integer> worklist = new ArrayDeque<>(1);
        worklist.add(0);
        this.tryblocks.stream()
            .map(BytecodeTryCatchBlock.class::cast)
            .map(BytecodeTryCatchBlock::handler)
            .map(this::index)
            .forEach(worklist::add);
        int MAX_RESULT = 0;
        Map<Integer, Map<Integer, Integer>> visited = new HashMap<>(0);
        while (!worklist.isEmpty()) {
            int current = worklist.pop();
            Map<Integer, Integer> vars = new HashMap<>(
                allVariables.getOrDefault(current, new HashMap<>(0)));
            while (current < this.instructions.size()) {
                BytecodeEntry entry = this.instructions.get(current);
                final String aopcode = entry.testCode();
                System.out.println(aopcode);
                if (entry instanceof BytecodeInstruction) {
                    final BytecodeInstruction var = BytecodeInstruction.class.cast(entry);
                    if (var.isBranchInstruction()) {
                        if (var.isSwitchInstruction()) {
                            final List<Label> offsets = var.offsets();
                            for (Label offset : offsets) {
                                final int target = this.index(offset);
                                final int sum = this.size(vars);
                                if (!visited.containsKey(target)
                                    || size(visited.get(target)) < sum) {
                                    visited.put(target, new HashMap<>(vars));
                                    worklist.add(target);
                                }
                            }
                            break;
                        } else if (var.isConditionalBranchInstruction()) {
                            final int jump = this.index(var.offset());
                            final int sum = size(vars);
                            if (!visited.containsKey(jump) || size(visited.get(jump)) < sum) {
                                visited.put(jump, vars);
                                worklist.add(jump);
                            }
                            final int next = current + 1;
                            if (!visited.containsKey(next) || size(visited.get(next)) < sum) {
                                visited.put(next, vars);
                                worklist.add(next);
                            }
                            break;
                        } else if (var.isReturnInstruction()) {
                            break;
                        } else {
                            final int jump = this.index(var.offset());
                            final int sum = size(vars);
                            if (!visited.containsKey(jump) || size(visited.get(jump)) < sum) {
                                visited.put(jump, vars);
                                worklist.add(jump);
                            }
                            break;
                        }
                    }
                    if (var.isVarInstruction()) {
                        if (var.size() == 2) {
                            final int key = var.localIndex();
                            vars.put(key, 2);
                        } else {
                            final int key = var.localIndex();
                            vars.put(key, 1);
                        }
                    }
                    if (allVariables.get(current) == null || size(vars) > size(
                        allVariables.get(current))) {
                        allVariables.put(current, new HashMap<>(vars));
                    }
                }
                current++;
            }
        }
        return allVariables.values().stream()
            .mapToInt(vars -> vars.values().stream().mapToInt(Integer::intValue).sum())
            .max()
            .orElse(0);
    }


    private int computeLocalsWithCFG2() {
        Logger.info(this, "Computing locals for %s", this.properties);
        Variables initial = new Variables();
        int first = 0;
        if (!this.properties.isStatic()) {
            initial.put(0, 1);
            first = 1;
        }
        final Type[] types = Type.getArgumentTypes(this.properties.descriptor());
        for (int index = 0; index < types.length; index++) {
            final Type type = types[index];
            initial.put(index * type.getSize() + first, type.getSize());
        }
        Map<Integer, Variables> all = new TreeMap<>();
        Map<Integer, Variables> worklist = new HashMap<>();
        worklist.put(0, initial);
        final int total = this.instructions.size();
        Variables currentVars;
        while (!worklist.isEmpty()) {
            final Map.Entry<Integer, Variables> curr = worklist.entrySet()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(""));
            int current = curr.getKey();
            worklist.remove(current);
            if (all.get(current) != null) {
                continue;
            }
            currentVars = new Variables(curr.getValue());
            while (current < total) {
                BytecodeEntry entry = this.instructions.get(current);
                if (entry instanceof BytecodeInstruction) {
                    final BytecodeInstruction var = BytecodeInstruction.class.cast(entry);
                    if (var.isBranchInstruction()) {
                        if (var.isSwitchInstruction()) {
                            final List<Label> offsets = var.offsets();
                            for (Label offset : offsets) {
                                final int target = this.index(offset);
                                worklist.put(target, new Variables(currentVars));
                            }
                            break;
                        } else if (var.isConditionalBranchInstruction()) {
                            final int jump = this.index(var.offset());
                            worklist.put(jump, new Variables(currentVars));
                            final int next = current + 1;
                            worklist.put(next, new Variables(currentVars));
                            break;
                        } else if (var.isReturnInstruction()) {
                            break;
                        } else {
                            final int jump = this.index(var.offset());
                            worklist.put(jump, new Variables(currentVars));
                            break;
                        }
                    }
                    if (var.isVarInstruction()) {
                        currentVars.put(var);
                    }
                }
                final Variables value = new Variables(currentVars);
                this.catches(current).stream().forEach(ind -> {
                    worklist.put(ind, new Variables(value));
                });
                all.put(current, value);
                current++;
            }
        }
//        __________
//        this.tryblocks.stream().map(BytecodeTryCatchBlock.class::cast)
//            .forEach(block -> {
//                    final int start = this.index(block.start());
//                    final int end = this.index(block.end());
//                    final List<Variables> collect = all.entrySet().stream().filter(
//                        entry -> entry.getKey() >= start && entry.getKey() <= end
//                    ).map(Map.Entry::getValue).collect(Collectors.toList());
//                    if (!collect.isEmpty()) {
//                        Variables max = collect.get(0);
//                        for (final Variables variables : collect) {
//                            if (variables.size() > max.size()) {
//                                max = variables;
//                            }
//                        }
//                        worklist.put(
//                            this.index(block.handler()),
//                            new Variables(max)
//                        );
//                    }
//                }
//            );
// ----------
//        while (!worklist.isEmpty()) {
//            final Map.Entry<Integer, Variables> curr = worklist.entrySet()
//                .stream()
//                .findFirst()
//                .orElseThrow(() -> new IllegalStateException(""));
//            int current = curr.getKey();
//            worklist.remove(current);
//            if (all.get(current) != null) {
//                continue;
//            }
//            currentVars = new Variables(curr.getValue());
//            while (current < total) {
//                BytecodeEntry entry = this.instructions.get(current);
//                if (entry instanceof BytecodeInstruction) {
//                    final BytecodeInstruction var = BytecodeInstruction.class.cast(entry);
//                    if (var.isBranchInstruction()) {
//                        if (var.isSwitchInstruction()) {
//                            final List<Label> offsets = var.offsets();
//                            for (Label offset : offsets) {
//                                final int target = this.index(offset);
//                                worklist.put(target, new Variables(currentVars));
//                            }
//                            break;
//                        } else if (var.isConditionalBranchInstruction()) {
//                            final int jump = this.index(var.offset());
//                            worklist.put(jump, new Variables(currentVars));
//                            final int next = current + 1;
//                            worklist.put(next, new Variables(currentVars));
//                            break;
//                        } else if (var.isReturnInstruction()) {
//                            break;
//                        } else {
//                            final int jump = this.index(var.offset());
//                            worklist.put(jump, new Variables(currentVars));
//                            break;
//                        }
//                    }
//                    if (var.isVarInstruction()) {
//                        currentVars.put(var);
//                    }
//                }
//                final Variables value = new Variables(currentVars);
//                this.catches(current).stream().forEach(ind -> {
//                    worklist.put(ind, new Variables(value));
//                });
//                all.put(current, value);
//                current++;
//            }
//        }

        return all.values().stream().mapToInt(Variables::size).max().orElse(0);
    }


    private List<Integer> catches(final int instruction) {
        return this.tryblocks.stream().map(BytecodeTryCatchBlock.class::cast)
            .filter(
                block -> index(block.start()) <= instruction
                    && index(block.end()) >= instruction
            ).map(
                block -> index(block.handler())
            ).collect(Collectors.toList());
    }

    @ToString
    private static class Variables {

        private final Map<Integer, Integer> all;

        public Variables() {
            this(new HashMap<>(0));
        }

        public Variables(final Variables vars) {
            this(vars.all);
        }

        public Variables(final Map<Integer, Integer> all) {
            this.all = new HashMap<>(all);
        }

        int size() {
            return this.all.values().stream().mapToInt(Integer::intValue).sum();
        }

        public void put(BytecodeInstruction var) {
            this.put(var.localIndex(), var.size());
        }

        public void put(int index, int value) {
            this.all.put(index, value);
        }

        public void putSlot(int index) {
            this.all.put(index, 1);
        }

        public void putTwoSlots(int index) {
            this.all.put(index, 2);
        }
    }

    private int size(Map<Integer, Integer> variables) {
        return variables.values().stream().mapToInt(Integer::intValue).sum();
    }

    private int computeLocalsWithCFG(
        Map<Integer, Integer> variables,
        int currentIndex,
        Set<Integer> visited
    ) {
        final int size = this.instructions.size();
        for (int index = currentIndex; index < size; ++index) {
            final BytecodeEntry instruction = this.instructions.get(index);
            if (instruction instanceof BytecodeInstruction) {
                final BytecodeInstruction var = BytecodeInstruction.class.cast(instruction);
                if (var.isConditionalBranchInstruction()) {
                    final int jump = this.index(var.offset());
                    final int branch;
                    if (visited.contains(jump)) {
                        branch = 0;
                    } else {
                        visited.add(jump);
                        branch = this.computeLocalsWithCFG(
                            new HashMap<>(variables), jump, new HashSet<>(visited));
                    }
                    final int nextIndex = index + 1;
                    final int nxt;
                    if (visited.contains(nextIndex)) {
                        nxt = 0;
                    } else {
                        visited.add(nextIndex);
                        nxt = this.computeLocalsWithCFG(
                            new HashMap<>(variables), nextIndex, new HashSet<>(visited));
                    }
                    return Math.max(
                        Math.max(branch, nxt),
                        variables.values().stream().mapToInt(Integer::intValue).sum()
                    );
                } else if (var.isReturnInstruction()) {
                    return variables.values().stream().mapToInt(Integer::intValue).sum();
                } else if (var.isSwitchInstruction()) {
                    final List<Label> offsets = var.offsets();
                    int max = 0;
                    for (Label offset : offsets) {
                        final int index1 = this.index(offset);
                        if (!visited.contains(index1)) {
                            visited.add(index1);
                            max = Math.max(
                                max,
                                this.computeLocalsWithCFG(
                                    new HashMap<>(variables),
                                    index1,
                                    new HashSet<>(visited)
                                )
                            );
                        }
                    }
                    return max;
                } else if (var.isBranchInstruction()) {
                    final int jump = this.index(var.offset());
                    if (visited.contains(jump)) {
                        return variables.values().stream().mapToInt(Integer::intValue).sum();
                    } else {
                        visited.add(jump);
                        return this.computeLocalsWithCFG(
                            new HashMap<>(variables), jump, new HashSet<>(visited));
                    }
                }
                if (var.isVarInstruction()) {
                    if (var.size() == 2) {
                        final int key = var.localIndex();
                        variables.put(key, 2);
                    } else {
                        final int key = var.localIndex();
                        variables.put(key, 1);
                    }
                }
            }
        }
        return variables.values().stream().mapToInt(Integer::intValue).sum();
    }
}
