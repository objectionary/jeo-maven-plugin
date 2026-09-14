/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

/**
 * Compilation manager that stores the bytecode during compilation.
 *
 * @since 0.1.0
 */
final class BytecodeManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    /**
     * Bytecode.
     */
    private final AtomicReference<BytecodeOutput> output;

    /**
     * Constructor.
     *
     * @param compiler Java compiler
     */
    BytecodeManager(final JavaCompiler compiler) {
        this(
            compiler.getStandardFileManager(
                null,
                Locale.getDefault(),
                Charset.defaultCharset()
            )
        );
    }

    /**
     * Constructor.
     *
     * @param manager Standard Java file manager
     */
    private BytecodeManager(final StandardJavaFileManager manager) {
        super(manager);
        this.output = new AtomicReference<>();
    }

    @Override
    public JavaFileObject getJavaFileForOutput(
        final Location location,
        final String classname,
        final JavaFileObject.Kind kind,
        final FileObject sibling
    ) {
        this.output.set(new BytecodeOutput(classname));
        return this.output.get();
    }

    /**
     * Get the bytecode.
     *
     * @return Bytecode
     */
    byte[] bytecode() {
        return this.output.get().bytecode();
    }
}
