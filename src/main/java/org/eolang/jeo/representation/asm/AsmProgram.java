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
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameter;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameters;
import org.eolang.jeo.representation.bytecode.BytecodeMethodProperties;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;
import org.eolang.jeo.representation.bytecode.BytecodeTryCatchBlock;
import org.eolang.jeo.representation.bytecode.InnerClass;
import org.eolang.jeo.representation.xmir.AllLabels;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InnerClassNode;
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

/**
 * ASM bytecode parser.
 * We used to use the Visitor pattern, but it's too verbose and not very readable.
 * So, we decided to switch to a Tree-API-based approach.
 * You can read more about different approaches right here:
 * <a href="https://asm.ow2.io/asm4-guide.pdf">https://asm.ow2.io/asm4-guide.pdf</a>
 * The recent version with the Visitor pattern is still available in the history:
 * <a href="https://github.com/objectionary/jeo-maven-plugin/tree/29daa0a167b5c2ba4caaceafb6e6bafc381ac05c">github</a>
 * @since 0.6
 * @todo #537:60min Refactor {@link AsmProgram} class.
 *  It's too big and contains a lot of methods.
 *  We need to refactor it to make it more readable and maintainable.
 *  Maybe it's worth splitting it into several classes.
 *  Don't forget to add/update the tests.
 *  Don't forget to remove PMD and Checkstyle suppressions.
 * @checkstyle CyclomaticComplexityCheck (500 lines)
 * @checkstyle JavaNCSSCheck (500 lines)
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 * @checkstyle MethodLengthCheck (500 lines)
 * @checkstyle AnonInnerLengthCheck (500 lines)
 */
@SuppressWarnings({
    "PMD.CouplingBetweenObjects",
    "PMD.TooManyMethods",
    "PMD.NcssCount",
    "PMD.UncommentedEmptyMethodBody",
    "PMD.ExcessiveMethodLength",
    "PMD.GodClass"
})
public final class AsmProgram {

    /**
     * Bytecode as plain bytes.
     */
    private final byte[] bytes;

    /**
     * Constructor.
     * @param bytes Bytes.
     */
    public AsmProgram(final byte... bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * Convert to bytecode.
     * @return Bytecode.
     */
    public BytecodeProgram bytecode() {
        final ClassNode node = new ClassNode();
        new ClassReader(this.bytes).accept(node, 0);
        final ClassName full = new ClassName(node.name);
        return new BytecodeProgram(
            full.pckg(),
            AsmProgram.clazz(node)
        );
    }

    /**
     * Convert asm class to domain class.
     * @param node Asm class node.
     * @return Domain class.
     */
    private static BytecodeClass clazz(final ClassNode node) {
        final ClassName full = new ClassName(node.name);
        return new BytecodeClass(
            full.name(),
            AsmProgram.methods(node),
            AsmProgram.fields(node),
            AsmProgram.annotations(node),
            AsmProgram.innerClasses(node),
            new BytecodeClassProperties(
                node.version,
                node.access,
                node.signature,
                node.superName,
                node.interfaces.toArray(new String[0])
            )
        );
    }

    /**
     * Convert asm field to domain field.
     * @param node Asm field node.
     * @return Domain field.
     */
    private static Collection<BytecodeField> fields(final ClassNode node) {
        return node.fields.stream().map(AsmProgram::field).collect(Collectors.toList());
    }

    /**
     * Convert asm field to domain field.
     * @param node Asm field node.
     * @return Domain field.
     */
    private static BytecodeField field(final FieldNode node) {
        return new BytecodeField(
            node.name,
            node.desc,
            node.signature,
            node.value,
            node.access,
            AsmProgram.annotations(node)
        );
    }

    /**
     * Convert asm methods to domain methods.
     * @param node Asm class node.
     * @return Domain methods.
     */
    private static Collection<BytecodeMethod> methods(final ClassNode node) {
        return node.methods.stream().map(AsmProgram::method).collect(Collectors.toList());
    }

    /**
     * Convert asm method to domain method.
     * @param node Asm method node.
     * @return Domain method.
     */
    private static BytecodeMethod method(final MethodNode node) {
        return new BytecodeMethod(
            AsmProgram.tryblocks(node),
            AsmProgram.instructions(node),
            AsmProgram.annotations(node),
            new BytecodeMethodProperties(
                node.access,
                node.name,
                node.desc,
                node.signature,
                AsmProgram.parameters(node),
                node.exceptions.toArray(new String[0])
            ),
            AsmProgram.defvalues(node),
            AsmProgram.maxs(node)
        );
    }

    /**
     * Convert asm method to domain method parameters.
     * @param node Asm method node.
     * @return Domain method parameters.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BytecodeMethodParameters parameters(final MethodNode node) {
        final List<AnnotationNode>[] invisible;
        if (node.invisibleParameterAnnotations == null) {
            invisible = new List[Optional.ofNullable(node.visibleParameterAnnotations)
                .orElse(new List[0]).length];
        } else {
            invisible = node.invisibleParameterAnnotations;
        }
        final List<AnnotationNode>[] visible;
        if (node.visibleParameterAnnotations == null) {
            visible = new List[Optional.ofNullable(node.invisibleParameterAnnotations)
                .orElse(new List[0]).length];
        } else {
            visible = node.visibleParameterAnnotations;
        }
        final Type[] types = Type.getArgumentTypes(node.desc);
        final List<BytecodeMethodParameter> params = new ArrayList<>(types.length);
        for (int index = 0; index < types.length; ++index) {
            final List<BytecodeAnnotation> annotations = new ArrayList<>(0);
            if (visible.length > index) {
                annotations.addAll(
                    AsmProgram.safe(visible[index], true).collect(Collectors.toList())
                );
            }
            if (invisible.length > index) {
                annotations.addAll(
                    AsmProgram.safe(invisible[index], false).collect(Collectors.toList())
                );
            }
            params.add(
                new BytecodeMethodParameter(
                    index,
                    types[index],
                    annotations
                )
            );
        }
        return new BytecodeMethodParameters(params);
    }

    /**
     * Convert asm method to domain method maxs.
     * @param node Asm method node.
     * @return Domain method maxs.
     */
    private static BytecodeMaxs maxs(final MethodNode node) {
        return new BytecodeMaxs(node.maxStack, node.maxLocals);
    }

    /**
     * Convert asm method to domain method tryblocks.
     * @param node Asm method node.
     * @return Domain method tryblocks.
     */
    private static List<BytecodeEntry> tryblocks(final MethodNode node) {
        return node.tryCatchBlocks.stream().map(AsmProgram::tryblock).collect(Collectors.toList());
    }

    /**
     * Convert asm try-catch block to domain try-catch block.
     * @param node Asm try-catch block node.
     * @return Domain try-catch block.
     */
    private static BytecodeEntry tryblock(final TryCatchBlockNode node) {
        return new BytecodeTryCatchBlock(
            node.start.getLabel(),
            node.end.getLabel(),
            node.handler.getLabel(),
            node.type
        );
    }

    /**
     * Retrieve domain attributes from asm class.
     * @param node Asm class node.
     * @return Domain attributes.
     */
    private static Collection<BytecodeAttribute> innerClasses(final ClassNode node) {
        return node.innerClasses.stream()
            .map(AsmProgram::innerClass)
            .collect(Collectors.toList());
    }

    /**
     * Convert asm inner class to domain inner class.
     * @param node Asm inner class node.
     * @return Domain inner class.
     */
    private static BytecodeAttribute innerClass(final InnerClassNode node) {
        return new InnerClass(
            node.name,
            node.outerName,
            node.innerName,
            node.access
        );
    }

    /**
     * Convert asm class to domain class.
     * @param node Asm class node.
     * @return Domain class.
     */
    private static List<BytecodeEntry> instructions(final MethodNode node) {
        return Arrays.stream(node.instructions.toArray())
            .map(AsmProgram::instruction)
            .collect(Collectors.toList());
    }

    /**
     * Convert asm instruction to domain instruction.
     * @param node Asm instruction node.
     * @return Domain instruction.
     */
    private static BytecodeEntry instruction(final AbstractInsnNode node) {
        final BytecodeEntry result;
        switch (node.getType()) {
            case AbstractInsnNode.INSN:
                result = new BytecodeInstructionEntry(node.getOpcode());
                break;
            case AbstractInsnNode.INT_INSN:
                final IntInsnNode instr = IntInsnNode.class.cast(node);
                result = new BytecodeInstructionEntry(
                    instr.getOpcode(),
                    instr.operand
                );
                break;
            case AbstractInsnNode.VAR_INSN:
                final VarInsnNode varinstr = VarInsnNode.class.cast(node);
                result = new BytecodeInstructionEntry(
                    varinstr.getOpcode(), varinstr.var
                );
                break;
            case AbstractInsnNode.TYPE_INSN:
                final TypeInsnNode typeinstr = TypeInsnNode.class.cast(node);
                result = new BytecodeInstructionEntry(
                    typeinstr.getOpcode(),
                    typeinstr.desc
                );
                break;
            case AbstractInsnNode.FIELD_INSN:
                final FieldInsnNode field = FieldInsnNode.class.cast(node);
                result = new BytecodeInstructionEntry(
                    field.getOpcode(),
                    field.owner,
                    field.name,
                    field.desc
                );
                break;
            case AbstractInsnNode.METHOD_INSN:
                final MethodInsnNode method = MethodInsnNode.class.cast(node);
                result = new BytecodeInstructionEntry(
                    method.getOpcode(),
                    method.owner,
                    method.name,
                    method.desc,
                    method.itf
                );
                break;
            case AbstractInsnNode.INVOKE_DYNAMIC_INSN:
                final InvokeDynamicInsnNode dynamic = InvokeDynamicInsnNode.class.cast(node);
                result = new BytecodeInstructionEntry(
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
                break;
            case AbstractInsnNode.JUMP_INSN:
                final JumpInsnNode jump = JumpInsnNode.class.cast(node);
                result = new BytecodeInstructionEntry(
                    jump.getOpcode(),
                    jump.label.getLabel()
                );
                break;
            case AbstractInsnNode.LABEL:
                final LabelNode label = LabelNode.class.cast(node);
                result = new BytecodeLabel(
                    label.getLabel(),
                    new AllLabels()
                );
                break;
            case AbstractInsnNode.LDC_INSN:
                final LdcInsnNode ldc = LdcInsnNode.class.cast(node);
                result = new BytecodeInstructionEntry(
                    ldc.getOpcode(),
                    ldc.cst
                );
                break;
            case AbstractInsnNode.IINC_INSN:
                final IincInsnNode iinc = IincInsnNode.class.cast(node);
                result = new BytecodeInstructionEntry(
                    iinc.getOpcode(),
                    iinc.var,
                    iinc.incr
                );
                break;
            case AbstractInsnNode.TABLESWITCH_INSN:
                final TableSwitchInsnNode table = TableSwitchInsnNode.class.cast(node);
                result = new BytecodeInstructionEntry(
                    table.getOpcode(),
                    Stream.concat(
                        Stream.of(table.min, table.max, table.dflt.getLabel()),
                        table.labels.stream().map(LabelNode::getLabel)
                    ).toArray(Object[]::new)
                );
                break;
            case AbstractInsnNode.LOOKUPSWITCH_INSN:
                final LookupSwitchInsnNode lookup = LookupSwitchInsnNode.class.cast(node);
                result = new BytecodeInstructionEntry(
                    lookup.getOpcode(),
                    Stream.concat(
                        Stream.of(lookup.dflt.getLabel()),
                        Stream.concat(
                            lookup.keys.stream(),
                            lookup.labels.stream().map(LabelNode::getLabel)
                        )
                    ).toArray(Object[]::new)
                );
                break;
            case AbstractInsnNode.MULTIANEWARRAY_INSN:
                final MultiANewArrayInsnNode multiarr = MultiANewArrayInsnNode.class.cast(node);
                result = new BytecodeInstructionEntry(
                    multiarr.getOpcode(),
                    multiarr.desc,
                    multiarr.dims
                );
                break;
            case AbstractInsnNode.FRAME:
                final FrameNode frame = FrameNode.class.cast(node);
                result = new BytecodeFrame(frame.type, frame.local, frame.stack);
                break;
            case AbstractInsnNode.LINE:
                result = new BytecodeEntry() {
                    @Override
                    public void writeTo(final MethodVisitor ignore) {
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
                break;
            default:
                throw new IllegalStateException(
                    String.format("Unknown instruction type: %s", node)
                );
        }
        return result;
    }

    /**
     * Retrieve domain annotations from asm class.
     * @param node Asm class node.
     * @return Domain annotations.
     */
    private static Collection<BytecodeAnnotation> annotations(final ClassNode node) {
        return Stream.concat(
            AsmProgram.safe(node.visibleAnnotations, true),
            AsmProgram.safe(node.invisibleAnnotations, false)
        ).collect(Collectors.toList());
    }

    /**
     * Retrieve domain annotations from asm method.
     * @param node Asm method node.
     * @return Domain annotations.
     */
    private static List<BytecodeAnnotation> annotations(final MethodNode node) {
        return Stream.concat(
            AsmProgram.safe(node.visibleAnnotations, true),
            AsmProgram.safe(node.invisibleAnnotations, false)
        ).collect(Collectors.toList());
    }

    /**
     * Retrieve domain annotations from asm field.
     * @param node Asm field node.
     * @return Domain annotations.
     */
    private static BytecodeAnnotations annotations(final FieldNode node) {
        return new BytecodeAnnotations(
            Stream.concat(
                AsmProgram.safe(node.visibleAnnotations, true),
                AsmProgram.safe(node.invisibleAnnotations, false)
            )
        );
    }

    /**
     * Safe annotations.
     * @param nodes Annotation nodes.
     * @param visible Is it visible?
     * @return Annotations.
     */
    private static Stream<BytecodeAnnotation> safe(
        final List<AnnotationNode> nodes, final boolean visible
    ) {
        return Optional.ofNullable(nodes)
            .orElse(new ArrayList<>(0))
            .stream()
            .map(ann -> AsmProgram.annotation(ann, visible));
    }

    /**
     * Convert asm annotation to domain annotation.
     * @param node Asm annotation node.
     * @param visible Is it visible?
     * @return Domain annotation.
     */
    private static BytecodeAnnotation annotation(final AnnotationNode node, final boolean visible) {
        final List<BytecodeAnnotationValue> properties = new ArrayList<>(0);
        final List<Object> values = Optional.ofNullable(node.values)
            .orElse(new ArrayList<>(0));
        for (int index = 0; index < values.size(); index += 2) {
            properties.add(
                AsmProgram.annotationProperty(
                    (String) values.get(index),
                    values.get(index + 1)
                )
            );
        }
        return new BytecodeAnnotation(node.desc, visible, properties);
    }

    /**
     * Convert asm annotation property to domain annotation property.
     * @param name Property name.
     * @param value Property value.
     * @return Domain annotation.
     */
    private static BytecodeAnnotationValue annotationProperty(
        final String name, final Object value
    ) {
        final BytecodeAnnotationValue result;
        if (value instanceof String[]) {
            final String[] params = (String[]) value;
            result = BytecodeAnnotationProperty.enump(name, params[0], params[1]);
        } else if (value instanceof AnnotationNode) {
            final AnnotationNode cast = AnnotationNode.class.cast(value);
            result = BytecodeAnnotationProperty.annotation(
                name,
                cast.desc,
                cast.values.stream().map(
                    val -> AsmProgram.annotationProperty("", val)
                ).collect(Collectors.toList())
            );
        } else if (value instanceof List) {
            result = BytecodeAnnotationProperty.array(
                name,
                ((Collection<?>) value).stream()
                    .map(val -> AsmProgram.annotationProperty("", val))
                    .collect(Collectors.toList())
            );
        } else {
            result = BytecodeAnnotationProperty.plain(name, value);
        }
        return result;
    }

    /**
     * Convert asm default value to domain default value.
     * @param node Asm method node.
     * @return Domain default value.
     */
    private static List<BytecodeDefaultValue> defvalues(final MethodNode node) {
        final List<BytecodeDefaultValue> result;
        if (node.annotationDefault == null) {
            result = Collections.emptyList();
        } else {
            result = Collections.singletonList(
                new BytecodeDefaultValue(
                    AsmProgram.annotationProperty(null, node.annotationDefault)
                )
            );
        }
        return result;
    }
}
