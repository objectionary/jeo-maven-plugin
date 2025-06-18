/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.bytecode.BytecodeAttributes;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeClassProperties;
import org.eolang.jeo.representation.bytecode.BytecodeField;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.InnerClass;
import org.objectweb.asm.tree.ClassNode;

/**
 * <p>ASM-based bytecode parser for Java classes.</p>
 * <p>This class provides functionality to parse ASM ClassNode objects and convert
 * them into domain-specific bytecode representations. It handles extraction of
 * class properties, methods, fields, and annotations from ASM's internal structure.</p>
 * @since 0.6.0
 */
public final class AsmClass {

    /**
     * Class node.
     */
    private final ClassNode node;

    /**
     * Constructor.
     * @param node The ASM class node to parse
     */
    AsmClass(final ClassNode node) {
        this.node = node;
    }

    /**
     * Convert ASM class to domain bytecode class.
     * @return The domain bytecode class representation
     */
    public BytecodeClass bytecode() {
        final ClassName full = new ClassName(this.node.name);
        return new BytecodeClass(
            full.name(),
            this.methods(),
            this.fields(),
            new AsmAnnotations(this.node).bytecode(),
            this.attributes(),
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
     * Convert ASM fields to domain fields.
     * @return The list of domain field representations
     */
    private List<BytecodeField> fields() {
        return this.node.fields.stream()
            .map(AsmField::new)
            .map(AsmField::bytecode)
            .collect(Collectors.toList());
    }

    /**
     * Convert ASM methods to domain methods.
     * @return The list of domain method representations
     */
    private List<BytecodeMethod> methods() {
        return this.node.methods.stream()
            .map(AsmMethod::new)
            .map(AsmMethod::bytecode)
            .collect(Collectors.toList());
    }

    /**
     * Retrieve domain attributes from ASM class.
     * @return The domain attributes representation
     */
    private BytecodeAttributes attributes() {
        return new BytecodeAttributes(
            this.node.innerClasses.stream().map(
                clazz -> new InnerClass(
                    clazz.name,
                    clazz.outerName,
                    clazz.innerName,
                    clazz.access
                )
            ).collect(Collectors.toList())
        );
    }
}
