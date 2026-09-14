/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Collections;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.cactoos.Input;
import org.cactoos.io.ResourceOf;

/**
 * Java source class with ".java" extension.
 *
 * @since 0.1
 */
@SuppressWarnings("JTCOP.RuleCorrectTestName")
public final class JavaSourceClass {

    /**
     * Name of Java class.
     */
    private final String name;

    /**
     * Source of Java class.
     */
    private final Input java;

    /**
     * Constructor.
     *
     * @param resource Resource of Java class
     */
    public JavaSourceClass(final String resource) {
        this(JavaSourceClass.filename(resource), new ResourceOf(resource));
    }

    /**
     * Constructor.
     *
     * @param filename Name of Java class
     * @param java Source of Java class
     */
    private JavaSourceClass(final String filename, final Input java) {
        this.name = filename;
        this.java = java;
    }

    /**
     * Compile the Java class.
     *
     * @return Bytecode of compiled class
     */
    public Bytecode compile() {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final BytecodeManager manager = new BytecodeManager(compiler);
        final boolean successful = compiler.getTask(
            null,
            manager,
            null,
            null,
            null,
            Collections.singleton(new SourceCode(this.name, this.java))
        ).call();
        if (successful) {
            return new Bytecode(manager.bytecode());
        } else {
            throw new IllegalStateException(
                String.format("Compilation failed for class %s", this.name)
            );
        }
    }

    private static String filename(final String resource) {
        return resource.substring(resource.lastIndexOf('/') + 1);
    }
}
