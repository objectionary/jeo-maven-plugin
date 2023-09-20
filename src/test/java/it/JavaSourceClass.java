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

import org.cactoos.Input;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.cactoos.io.ResourceOf;

/**
 * Java source class with ".java" extension.
 *
 * @since 0.1
 */
final class JavaSourceClass {

    /**
     * Source of Java class.
     */
    private final Input java;

    /**
     * Constructor.
     * @param resource Resource of Java class.
     */
    JavaSourceClass(final String resource) {
        this(new ResourceOf(resource));
    }

    /**
     * Constructor.
     * @param java Source of Java class.
     */
    private JavaSourceClass(final Input java) {
        this.java = java;
    }

    /**
     * Compile the Java class.
     * @return Bytecode of compiled class.
     * @todo #81:30min Compile Java class dynamically.
     *  Currently, we don't compile Java classes dynamically.
     *  Instead, we pass the source code directly.
     *  This is a temporary solution. We need to compile the Java class
     *  dynamically and pass the bytecode to the test.
     *  When this is done, remove that puzzle from this method.
     */
    byte[] compile() {
        return new UncheckedBytes(new BytesOf(this.java)).asBytes();
    }
}
