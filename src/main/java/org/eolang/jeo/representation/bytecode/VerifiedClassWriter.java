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
import org.eolang.jeo.representation.DefaultVersion;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.SimpleVerifier;
import org.objectweb.asm.util.CheckClassAdapter;

/**
 * Class writer that verifies the bytecode.
 * @since 0.2
 */
public final class VerifiedClassWriter extends CustomClassWriter {

    @Override
    public byte[] toByteArray() {
        final byte[] bytes = super.toByteArray();
        this.verify(bytes);
        return bytes;
    }

    /**
     * Verify the bytecode.
     * @param bytes The bytecode to verify.
     */
    private void verify(final byte[] bytes) {
        ClassNode classNode = new ClassNode();
        new ClassReader(bytes).accept(
            new CheckClassAdapter(new DefaultVersion().api(), classNode, false) {
            },
            ClassReader.SKIP_DEBUG
        );
        List<MethodNode> methods = classNode.methods;
        Type syperType = classNode.superName == null ? null : Type.getObjectType(
            classNode.superName);
        List<Type> interfaces = new ArrayList<>();
        for (String interfaceName : classNode.interfaces) {
            interfaces.add(Type.getObjectType(interfaceName));
        }
        final String name = classNode.name;
        for (MethodNode method : methods) {
            try {
                SimpleVerifier verifier =
                    new SimpleVerifier(
                        Type.getObjectType(name),
                        syperType,
                        interfaces,
                        (classNode.access & Opcodes.ACC_INTERFACE) != 0
                    );
                Analyzer<BasicValue> analyzer = new Analyzer<>(verifier);
                verifier.setClassLoader(Thread.currentThread().getContextClassLoader());
                analyzer.analyze(name, method);
            } catch (final AnalyzerException exception) {
                throw new IllegalStateException(
                    String.format(
                        "Bytecode verification failed for the class '%s' and method '%s'",
                        name,
                        method
                    ),
                    exception
                );
            }
        }
    }
}
