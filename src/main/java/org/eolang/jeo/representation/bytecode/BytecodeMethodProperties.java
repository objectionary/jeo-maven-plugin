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

import com.jcabi.log.Logger;
import java.util.stream.IntStream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.JavaName;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Bytecode method properties.
 * @since 0.1
 */
@EqualsAndHashCode
@ToString
public final class BytecodeMethodProperties implements Testable {

    /**
     * Access modifiers.
     */
    private final int access;

    /**
     * Method name.
     */
    private final String name;

    /**
     * Method descriptor.
     */
    private final String descriptor;

    /**
     * Method signature.
     */
    private final String signature;

    /**
     * Method exceptions.
     */
    private final String[] exceptions;

    /**
     * Constructor.
     * @param name Method name.
     * @param descriptor Method descriptor.
     * @param modifiers Access modifiers.
     */
    public BytecodeMethodProperties(
        final String name,
        final String descriptor,
        final int... modifiers
    ) {
        this(name, descriptor, null, modifiers);
    }

    /**
     * Constructor.
     * @param name Method name.
     * @param descriptor Method descriptor.
     * @param signature Method signature.
     * @param modifiers Access modifiers.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public BytecodeMethodProperties(
        final String name,
        final String descriptor,
        final String signature,
        final int... modifiers
    ) {
        this(IntStream.of(modifiers).sum(), name, descriptor, signature, new String[0]);
    }

    /**
     * Constructor.
     * @param access Access modifiers.
     * @param name Method name.
     * @param descriptor Method descriptor.
     * @param signature Method signature.
     * @param exceptions Method exceptions.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    public BytecodeMethodProperties(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final String... exceptions
    ) {
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.exceptions = exceptions;
    }

    @Override
    public String testCode() {
        return String.format("\"%s\", \"%s\", %d", this.name, this.descriptor, this.access);
    }

    /**
     * Add method to class writer.
     * @param writer Class writer.
     * @return Method visitor.
     */
    MethodVisitor writeMethod(final ClassVisitor writer) {
        Logger.debug(
            this,
            String.format("Creating method visitor with the following properties %s", this)
        );
        return writer.visitMethod(
            this.access,
            new JavaName(this.name).decode(),
            this.descriptor,
            this.signature,
            this.exceptions
        );
    }
}
