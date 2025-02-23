/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
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
