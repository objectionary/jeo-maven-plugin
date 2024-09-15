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
package org.eolang.jeo.representation;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import org.objectweb.asm.ClassVisitor;

/**
 * Class name.
 * @since 0.1.0
 */
@SuppressWarnings("PMD.UseObjectForClearerAPI")
public final class ClassNameVisitor extends ClassVisitor {

    /**
     * Atomic reference to store class name.
     */
    private final AtomicReference<String> bag;

    /**
     * Constructor.
     */
    public ClassNameVisitor() {
        this(new AtomicReference<>());
    }

    /**
     * Constructor.
     * @param bag Atomic reference to store class name.
     */
    private ClassNameVisitor(final AtomicReference<String> bag) {
        this(new DefaultVersion().api(), bag);
    }

    /**
     * Constructor.
     * @param api ASM API version.
     * @param bag Atomic reference to store class name.
     */
    private ClassNameVisitor(final int api, final AtomicReference<String> bag) {
        super(api);
        this.bag = bag;
    }

    @Override
    public void visit(
        final int version,
        final int access,
        final String name,
        final String signature,
        final String supername,
        final String[] interfaces
    ) {
        this.bag.set(name);
        super.visit(version, access, name, signature, supername, interfaces);
    }

    /**
     * Get class name.
     * @return Class name.
     */
    public String asString() {
        final String last = this.bag.get();
        if (Objects.isNull(last)) {
            throw new IllegalStateException(
                "Class name is not set, bug is empty. Use #visit() method to set it."
            );
        }
        return last;
    }
}
