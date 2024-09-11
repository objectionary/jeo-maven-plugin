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
package org.eolang.jeo.representation.asm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotationProperty;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeClassProperties;
import org.eolang.jeo.representation.bytecode.BytecodeDefaultValue;
import org.eolang.jeo.representation.bytecode.BytecodeEntry;
import org.eolang.jeo.representation.bytecode.BytecodeField;
import org.eolang.jeo.representation.bytecode.BytecodeFrame;
import org.eolang.jeo.representation.bytecode.BytecodeInstructionEntry;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.eolang.jeo.representation.bytecode.BytecodeMaxs;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.BytecodeMethodProperties;
import org.eolang.jeo.representation.bytecode.BytecodeParameters;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;
import org.eolang.jeo.representation.bytecode.BytecodeTryCatchBlock;
import org.eolang.jeo.representation.xmir.AllLabels;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.xembly.Directive;

public final class ASMProgram {

    private final byte[] bytecode;

    public ASMProgram(final byte... bytes) {
        this.bytecode = bytes.clone();
    }

    public BytecodeProgram bytecode() {
        final ClassNode node = new ClassNode();
        new ClassReader(this.bytecode).accept(node, 0);
        final ClassName full = new ClassName(node.name);
        return new BytecodeProgram(
            full.pckg(),
            ASMProgram.clazz(node)
        );
    }

    private static BytecodeClass clazz(final ClassNode node) {
        final ClassName full = new ClassName(node.name);
        return new BytecodeClass(
            full.name(),
            ASMProgram.methods(node),
            ASMProgram.fields(node),
            ASMProgram.annotations(node),
            ASMProgram.innerClasses(node),
            new BytecodeClassProperties(
                node.version,
                node.access,
                node.signature,
                node.superName,
                node.interfaces.toArray(new String[0])
            )
        );
    }

    private static Collection<BytecodeField> fields(final ClassNode node) {
        return node.fields.stream().map(ASMProgram::field).collect(Collectors.toList());
    }

    private static BytecodeField field(final FieldNode node) {
        return new BytecodeField(
            node.name,
            node.desc,
            node.signature,
            node.value,
            node.access,
            ASMProgram.annotations(node)
        );
    }

    private static Collection<BytecodeMethod> methods(final ClassNode node) {
        return node.methods.stream().map(ASMProgram::method).collect(Collectors.toList());
    }

    private static BytecodeMethod method(final MethodNode node) {
        return new BytecodeMethod(
            ASMProgram.tryblocks(node),
            ASMProgram.instructions(node),
            ASMProgram.annotations(node),
            new BytecodeMethodProperties(
                node.access,
                node.name,
                node.desc,
                node.signature,
                node.exceptions.toArray(new String[0])
            ),
            ASMProgram.defvalues(node),
            ASMProgram.maxs(node)
        );
    }

    private static BytecodeMaxs maxs(final MethodNode node) {
        return new BytecodeMaxs(node.maxStack, node.maxLocals);
    }

    private static List<BytecodeEntry> tryblocks(final MethodNode node) {
        return node.tryCatchBlocks.stream().map(ASMProgram::tryblock).collect(Collectors.toList());
    }

    private static BytecodeEntry tryblock(final TryCatchBlockNode tryCatchBlockNode) {
        return new BytecodeTryCatchBlock(
            tryCatchBlockNode.start.getLabel(),
            tryCatchBlockNode.end.getLabel(),
            tryCatchBlockNode.handler.getLabel(),
            tryCatchBlockNode.type
        );
    }

    private static Collection<BytecodeAttribute> innerClasses(final ClassNode node) {
        return node.innerClasses.stream()
            .map(ASMProgram::innerClass)
            .collect(Collectors.toList());
    }

    private static BytecodeAttribute innerClass(final InnerClassNode node) {
        return new BytecodeAttribute.InnerClass(
            node.name,
            node.outerName,
            node.innerName,
            node.access
        );
    }

    private static List<BytecodeEntry> instructions(final MethodNode node) {
        return Arrays.stream(node.instructions.toArray())
            .map(ASMProgram::instruction)
            .collect(Collectors.toList());
    }

    private static BytecodeEntry instruction(final AbstractInsnNode node) {
        switch (node.getType()) {
            case AbstractInsnNode.INSN:
                return new BytecodeInstructionEntry(node.getOpcode());
            case AbstractInsnNode.INT_INSN:
                final IntInsnNode instr = IntInsnNode.class.cast(node);
                return new BytecodeInstructionEntry(
                    instr.getOpcode(),
                    instr.operand
                );
            case AbstractInsnNode.VAR_INSN:
                final VarInsnNode varinstr = VarInsnNode.class.cast(node);
                return new BytecodeInstructionEntry(
                    varinstr.getOpcode(), varinstr.var
                );
            case AbstractInsnNode.TYPE_INSN:
                final TypeInsnNode typeinstr = TypeInsnNode.class.cast(node);
                return new BytecodeInstructionEntry(
                    typeinstr.getOpcode(),
                    typeinstr.desc
                );
            case AbstractInsnNode.FIELD_INSN:
                final FieldInsnNode fieldInsnNode = FieldInsnNode.class.cast(node);
                return new BytecodeInstructionEntry(
                    fieldInsnNode.getOpcode(),
                    fieldInsnNode.owner,
                    fieldInsnNode.name,
                    fieldInsnNode.desc
                );
            case AbstractInsnNode.METHOD_INSN:
                final MethodInsnNode methodInsnNode = MethodInsnNode.class.cast(node);
                return new BytecodeInstructionEntry(
                    methodInsnNode.getOpcode(),
                    methodInsnNode.owner,
                    methodInsnNode.name,
                    methodInsnNode.desc,
                    methodInsnNode.itf
                );
            case AbstractInsnNode.INVOKE_DYNAMIC_INSN:
                final InvokeDynamicInsnNode dynamic = InvokeDynamicInsnNode.class.cast(node);
                return new BytecodeInstructionEntry(
                    dynamic.getOpcode(),
                    Stream.concat(
                        Stream.of(
                            dynamic.name,
                            dynamic.desc,
                            dynamic.bsm
                        ),
                        Arrays.stream(dynamic.bsmArgs)
                    ).toArray(Object[]::new)
                );
            case AbstractInsnNode.JUMP_INSN:
                final JumpInsnNode jump = JumpInsnNode.class.cast(node);
                return new BytecodeInstructionEntry(
                    jump.getOpcode(),
                    jump.label.getLabel()
                );
            case AbstractInsnNode.LABEL:
                final LabelNode label = LabelNode.class.cast(node);
                return new BytecodeLabel(
                    label.getLabel(),
                    new AllLabels()
                );
            case AbstractInsnNode.LDC_INSN:
                final LdcInsnNode ldc = LdcInsnNode.class.cast(node);
                return new BytecodeInstructionEntry(
                    ldc.getOpcode(),
                    ldc.cst
                );
            case AbstractInsnNode.IINC_INSN:
                final IincInsnNode iinc = IincInsnNode.class.cast(node);
                return new BytecodeInstructionEntry(
                    iinc.getOpcode(),
                    iinc.var,
                    iinc.incr
                );
            case AbstractInsnNode.TABLESWITCH_INSN:
                final TableSwitchInsnNode table = TableSwitchInsnNode.class.cast(node);
                return new BytecodeInstructionEntry(
                    table.getOpcode(),
                    Stream.concat(
                        Stream.of(table.min, table.max, table.dflt.getLabel()),
                        table.labels.stream().map(LabelNode::getLabel)
                    ).toArray(Object[]::new)
                );
            case AbstractInsnNode.LOOKUPSWITCH_INSN:
                final LookupSwitchInsnNode lookup = LookupSwitchInsnNode.class.cast(node);
                return new BytecodeInstructionEntry(
                    lookup.getOpcode(),
                    Stream.concat(
                        Stream.of(lookup.dflt.getLabel()),
                        Stream.concat(
                            lookup.keys.stream(),
                            lookup.labels.stream().map(LabelNode::getLabel)
                        )
                    ).toArray(Object[]::new)
                );
            case AbstractInsnNode.MULTIANEWARRAY_INSN:
                final MultiANewArrayInsnNode multiarr = MultiANewArrayInsnNode.class.cast(node);
                return new BytecodeInstructionEntry(
                    multiarr.getOpcode(),
                    multiarr.desc,
                    multiarr.dims
                );
            case AbstractInsnNode.FRAME:
                final FrameNode frame = FrameNode.class.cast(node);
                return new BytecodeFrame(frame.type, frame.local, frame.stack);
            case AbstractInsnNode.LINE:
                return new BytecodeEntry() {
                    @Override
                    public void writeTo(final MethodVisitor visitor) {

                    }

                    @Override
                    public Iterable<Directive> directives(final boolean counting) {
                        return Collections.emptyList();
                    }

                    @Override
                    public boolean isLabel() {
                        return false;
                    }

                    @Override
                    public boolean isOpcode() {
                        return false;
                    }

                    @Override
                    public String testCode() {
                        return "";
                    }
                };
            default:
                throw new IllegalStateException(
                    String.format("Unknown instruction type: %s", node)
                );
        }
    }

    private static Collection<BytecodeAnnotation> annotations(final ClassNode node) {
        return Stream.concat(
            ASMProgram.safe(node.visibleAnnotations, true),
            ASMProgram.safe(node.invisibleAnnotations, false)
        ).collect(Collectors.toList());
    }

    private static List<BytecodeAnnotation> annotations(final MethodNode node) {
        return Stream.concat(
            ASMProgram.safe(node.visibleAnnotations, true),
            ASMProgram.safe(node.invisibleAnnotations, false)
        ).collect(Collectors.toList());
    }

    private static BytecodeAnnotations annotations(final FieldNode node) {
        return new BytecodeAnnotations(
            Stream.concat(
                ASMProgram.safe(node.visibleAnnotations, true),
                ASMProgram.safe(node.invisibleAnnotations, false)
            )
        );
    }

    static Stream<BytecodeAnnotation> safe(List<AnnotationNode> nodes, boolean visible) {
        return Optional.ofNullable(nodes)
            .orElse(new ArrayList<>(0))
            .stream()
            .map(ann -> ASMProgram.annotation(ann, visible));
    }


    private static BytecodeAnnotation annotation(final AnnotationNode node, final boolean visible) {
        List<BytecodeAnnotationValue> properties = new ArrayList<>(0);
        final List<Object> values = Optional.ofNullable(node.values).orElse(new ArrayList<>(0));
        for (int index = 0; index < values.size(); index += 2) {
            properties.add(
                ASMProgram.annotationProperty(
                    (String) values.get(index),
                    values.get(index + 1)
                )
            );
        }
        return new BytecodeAnnotation(node.desc, visible, properties);
    }

    private static BytecodeAnnotationValue annotationProperty(
        final String name, final Object value
    ) {
        if (value instanceof String[]) {
            final String[] params = (String[]) value;
            return BytecodeAnnotationProperty.enump(name, params[0], params[1]);
        } else if (value instanceof AnnotationNode) {
            AnnotationNode annotation = (AnnotationNode) value;
            return ASMProgram.annotation(
                annotation,
                true
            );
        } else if (value instanceof List) {
            return BytecodeAnnotationProperty.array(
                name,
                ((Collection<?>) value).stream()
                    .map(val -> ASMProgram.annotationProperty(null, val))
                    .collect(Collectors.toList())
            );
        } else {
            return BytecodeAnnotationProperty.plain(name, value);
        }
    }

    private static List<BytecodeDefaultValue> defvalues(final MethodNode node) {
        if (node.annotationDefault == null) {
            return Collections.emptyList();
        } else {
            return Collections.singletonList(
                new BytecodeDefaultValue(
                    ASMProgram.annotationProperty(
                        null, node.annotationDefault)
                )
            );
        }
    }
}
