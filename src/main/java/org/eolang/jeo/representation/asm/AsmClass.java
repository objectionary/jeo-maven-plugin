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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotationAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeArrayAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.eolang.jeo.representation.bytecode.BytecodeAttributes;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeClassProperties;
import org.eolang.jeo.representation.bytecode.BytecodeDefaultValue;
import org.eolang.jeo.representation.bytecode.BytecodeEntry;
import org.eolang.jeo.representation.bytecode.BytecodeEnumAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeField;
import org.eolang.jeo.representation.bytecode.BytecodeFrame;
import org.eolang.jeo.representation.bytecode.BytecodeInstruction;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.eolang.jeo.representation.bytecode.BytecodeLine;
import org.eolang.jeo.representation.bytecode.BytecodeMaxs;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameter;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameters;
import org.eolang.jeo.representation.bytecode.BytecodeMethodProperties;
import org.eolang.jeo.representation.bytecode.BytecodePlainAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeTryCatchBlock;
import org.eolang.jeo.representation.bytecode.InnerClass;
import org.eolang.jeo.representation.bytecode.LocalVariable;
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
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * ASM bytecode parser for a class.
 * @since 0.6
 */
public final class AsmClass {

    private final ClassNode node;

    public AsmClass(final ClassNode node) {
        this.node = node;
    }

    public BytecodeClass bytecode() {
        final ClassName full = new ClassName(this.node.name);
        return new BytecodeClass(
            full.name(),
            AsmClass.methods(this.node),
            AsmClass.fields(this.node),
            new AsmAnnotations(this.node).annotations(),
            AsmClass.innerClasses(this.node),
            new BytecodeClassProperties(
                this.node.version,
                this.node.access,
                this.node.signature,
                this.node.superName,
                this.node.interfaces.toArray(new String[0])
            )
        );
    }

    /**
     * Convert asm field to domain field.
     * @param node Asm field node.
     * @return Domain field.
     */
    private static List<BytecodeField> fields(final ClassNode node) {
        return node.fields.stream()
            .map(AsmField::new)
            .map(AsmField::bytecode)
            .collect(Collectors.toList());
    }

    /**
     * Convert asm methods to domain methods.
     * @param node Asm class node.
     * @return Domain methods.
     */
    private static List<BytecodeMethod> methods(final ClassNode node) {
        return node.methods.stream().map(AsmClass::method).collect(Collectors.toList());
    }

    /**
     * Convert asm method to domain method.
     * @param node Asm method node.
     * @return Domain method.
     */
    private static BytecodeMethod method(final MethodNode node) {
        return new AsmMethod(node).bytecode();

//        return new BytecodeMethod(
//            AsmClass.tryblocks(node),
//            AsmClass.instructions(node),
//            new AsmAnnotations(node).annotations(),
//            new BytecodeMethodProperties(
//                node.access,
//                node.name,
//                node.desc,
//                node.signature,
//                AsmClass.parameters(node),
//                node.exceptions.toArray(new String[0])
//            ),
//            AsmClass.defvalues(node),
//            AsmClass.maxs(node),
//            AsmClass.attributes(node)
//        );
    }



    /**
     * Retrieve domain attributes from asm class.
     * @param node Asm class node.
     * @return Domain attributes.
     */
    private static BytecodeAttributes innerClasses(final ClassNode node) {
        return new BytecodeAttributes(
            node.innerClasses.stream()
                .map(AsmClass::innerClass)
                .collect(Collectors.toList())
        );
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
}
