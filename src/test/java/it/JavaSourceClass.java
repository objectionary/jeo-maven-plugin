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
package it;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.cactoos.Input;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;

/**
 * Java source class with ".java" extension.
 * @since 0.1
 */
@SuppressWarnings("JTCOP.RuleCorrectTestName")
final class JavaSourceClass {

    private final String name;

    /**
     * Source of Java class.
     */
    private final Input java;

    /**
     * Constructor.
     * @param resource Resource of Java class.
     */
    JavaSourceClass(final String resource) {
        this(JavaSourceClass.filename(resource), new ResourceOf(resource));
    }

    /**
     * Constructor.
     * @param java Source of Java class.
     */
    private JavaSourceClass(final String filename, final Input java) {
        this.name = filename;
        this.java = java;
    }

    /**
     * Compile the Java class.
     * @return Bytecode of compiled class.
     * @todo #81:30min Compile Java class dynamically.
     * Currently, we don't compile Java classes dynamically.
     * Instead, we pass the source code directly.
     * This is a temporary solution. We need to compile the Java class
     * dynamically and pass the bytecode to the test.
     * When this is done, remove that puzzle from this method.
     */
    byte[] compile() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        BytecodeManager manager = new BytecodeManager(compiler);
        boolean successful = compiler.getTask(
            null,
            manager,
            null,
            null,
            null,
            Collections.singleton(new SourceCode(this.name, this.java))
        ).call();
        if (successful) {
            return manager.bytecode();
        } else {
            throw new IllegalStateException("Compilation failed.");
        }
    }

    private static String filename(final String resource) {
        return resource.substring(resource.lastIndexOf('/') + 1);
    }

    private static final class BytecodeManager
        extends ForwardingJavaFileManager<StandardJavaFileManager> {
        private AtomicReference<Bytecode> output;

        private BytecodeManager(JavaCompiler compiler) {
            this(
                compiler.getStandardFileManager(
                    null,
                    Locale.getDefault(),
                    Charset.defaultCharset()
                ));
        }

        private BytecodeManager(StandardJavaFileManager manager) {
            super(manager);
            this.output = new AtomicReference<>();
        }

        @Override
        public JavaFileObject getJavaFileForOutput(
            Location location,
            String className,
            JavaFileObject.Kind kind,
            FileObject sibling
        ) {
            this.output.set(new Bytecode(className));
            return this.output.get();
        }

        byte[] bytecode() {
            return this.output.get().bytecode();
        }
    }

    private static final class SourceCode extends SimpleJavaFileObject {

        private final Input src;

        /**
         * Construct a SimpleJavaFileObject of the given kind and with the
         * given URI.
         */
        SourceCode(final String name, final Input input) {
            super(URI.create(name), Kind.SOURCE);
            this.src = input;
        }

        @Override
        public CharSequence getCharContent(final boolean ignoreEncodingErrors) {
            return new UncheckedText(new TextOf(this.src)).asString();
        }
    }

    private static final class Bytecode extends SimpleJavaFileObject {
        private final ByteArrayOutputStream output;

        Bytecode(String name) {
            super(URI.create(String.format("%s%s", name, Kind.CLASS.extension)), Kind.CLASS);
            this.output = new ByteArrayOutputStream();
        }

        @Override
        public OutputStream openOutputStream() {
            return this.output;
        }

        byte[] bytecode() {
            return this.output.toByteArray();
        }
    }
}
