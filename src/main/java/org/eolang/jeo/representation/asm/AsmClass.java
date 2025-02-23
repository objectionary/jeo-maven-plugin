/*
 * The MIT License (MIT)
 *
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
 * ASM bytecode parser for a class.
 * @since 0.6
 */
public final class AsmClass {

    /**
     * Class node.
     */
    private final ClassNode node;

    /**
     * Constructor.
     * @param node Class node.
     */
    AsmClass(final ClassNode node) {
        this.node = node;
    }

    /**
     * Convert asm class to domain class.
     * @return Domain class.
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
     * Convert asm field to domain field.
     * @return Domain field.
     */
    private List<BytecodeField> fields() {
        return this.node.fields.stream()
            .map(AsmField::new)
            .map(AsmField::bytecode)
            .collect(Collectors.toList());
    }

    /**
     * Convert asm methods to domain methods.
     * @return Domain methods.
     */
    private List<BytecodeMethod> methods() {
        return this.node.methods.stream()
            .map(AsmMethod::new)
            .map(AsmMethod::bytecode)
            .collect(Collectors.toList());
    }

    /**
     * Retrieve domain attributes from asm class.
     * @return Domain attributes.
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
