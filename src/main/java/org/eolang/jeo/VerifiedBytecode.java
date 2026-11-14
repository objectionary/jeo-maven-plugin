/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import com.jcabi.log.Logger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.SimpleVerifier;
import org.objectweb.asm.util.CheckClassAdapter;

/**
 * Verified bytecode.
 *
 * @since 0.15.0
 */
public final class VerifiedBytecode {

    /**
     * Bytecode to verify.
     */
    private final byte[] code;

    /**
     * Constructor.
     *
     * @param code Bytecode to verify
     */
    public VerifiedBytecode(final byte[] code) {
        this.code = code.clone();
    }

    /**
     * Verifies the bytecode.
     */
    public void verify() {
        final ClassNode clazz = new ClassNode();
        new ClassReader(this.code)
            .accept(new CheckClassAdapter(clazz, false), ClassReader.SKIP_DEBUG);
        final Optional<Type> syper = Optional.ofNullable(clazz.superName)
            .map(Type::getObjectType);
        final List<Type> interfaces = clazz.interfaces.stream().map(Type::getObjectType)
            .collect(Collectors.toList());
        for (final MethodNode method : clazz.methods) {
            try {
                final SimpleVerifier verifier =
                    new SimpleVerifier(
                        Type.getObjectType(clazz.name),
                        syper.orElse(null),
                        interfaces,
                        (clazz.access & Opcodes.ACC_INTERFACE) != 0
                    );
                verifier.setClassLoader(Thread.currentThread().getContextClassLoader());
                new Analyzer<>(verifier).analyze(clazz.name, method);
            } catch (final ClassFormatError | AnalyzerException exception) {
                throw new IllegalStateException(
                    String.format(
                        "Bytecode verification failed for the class '%s' and method '%s'",
                        clazz.name,
                        method.name
                    ),
                    exception
                );
            }
        }
        this.verifyJvm(clazz);
        Logger.info(
            BytecodeClasses.class,
            "Bytecode verification passed for the class '%s'",
            clazz.name
        );
    }

    private void verifyJvm(final ClassNode clazz) {
        if ((clazz.access & Opcodes.ACC_MODULE) == 0) {
            final String binary = clazz.name.replace('/', '.');
            try {
                Logger.debug(
                    VerifiedBytecode.class,
                    "JVM verified %d method(s) in '%s'",
                    Class.forName(
                        binary,
                        false,
                        this.isolatedLoader(binary)
                    ).getDeclaredMethods().length,
                    clazz.name
                );
            } catch (final VerifyError | ClassFormatError exception) {
                throw new IllegalStateException(
                    String.format(
                        "Bytecode verification failed for the class '%s'",
                        clazz.name
                    ),
                    exception
                );
            } catch (final ClassNotFoundException | NoClassDefFoundError | IllegalAccessError
                | SecurityException ex) {
                Logger.warn(
                    VerifiedBytecode.class,
                    "JVM bytecode verification skipped for '%s': %s",
                    clazz.name,
                    ex.getMessage()
                );
            }
        }
    }

    private ClassLoader isolatedLoader(final String binary) {
        return new ClassLoader(Thread.currentThread().getContextClassLoader()) {
            @Override
            protected Class<?> findClass(final String name) throws ClassNotFoundException {
                if (!name.equals(binary)) {
                    throw new ClassNotFoundException(name);
                }
                return this.defineClass(
                    binary,
                    VerifiedBytecode.this.code,
                    0,
                    VerifiedBytecode.this.code.length
                );
            }
        };
    }
}
