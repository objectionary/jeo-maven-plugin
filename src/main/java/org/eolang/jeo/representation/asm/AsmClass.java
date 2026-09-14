/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.eolang.jeo.representation.bytecode.BytecodeAttributes;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeClassProperties;
import org.eolang.jeo.representation.bytecode.BytecodeField;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.InnerClass;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.ModuleNode;

/**
 * ASM-based bytecode parser for Java classes.
 *
 * <p>This class provides functionality to parse ASM ClassNode objects and convert them into
 * domain-specific bytecode representations. It handles extraction of class properties, methods,
 * fields, and annotations from ASM's internal structure.</p>
 *
 * @since 0.6.0
 */
public final class AsmClass {

    /**
     * Class node.
     */
    private final ClassNode node;

    /**
     * Constructor.
     *
     * @param node The ASM class node to parse
     */
    AsmClass(final ClassNode node) {
        this.node = node;
    }

    /**
     * Convert ASM class to domain bytecode class.
     *
     * @return The domain bytecode class representation
     */
    public BytecodeClass bytecode() {
        return new BytecodeClass(
            new ClassName(this.node.name),
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

    private List<BytecodeField> fields() {
        return this.node.fields.stream()
            .map(AsmField::new)
            .map(AsmField::bytecode)
            .collect(Collectors.toList());
    }

    private List<BytecodeMethod> methods() {
        return this.node.methods.stream()
            .map(AsmMethod::new)
            .map(AsmMethod::bytecode)
            .collect(Collectors.toList());
    }

    private BytecodeAttributes attributes() {
        return new BytecodeAttributes(
            Stream.concat(
                Stream.of(
                    this.source(),
                    this.module(),
                    this.enclosing(),
                    this.nesthost(),
                    this.nestmembers(),
                    this.permitted(),
                    this.records()
                ).filter(Optional::isPresent).map(Optional::get),
                Stream.concat(
                    this.inners(),
                    new AsmUnknownAttributes(this.node).bytecode().stream()
                )
            ).collect(Collectors.toList())
        );
    }

    private Optional<BytecodeAttribute> source() {
        final Optional<BytecodeAttribute> result;
        if (this.node.sourceFile == null && this.node.sourceDebug == null) {
            result = Optional.empty();
        } else {
            result = Optional.of(
                new BytecodeAttribute.SourceFile(
                    this.node.sourceFile,
                    this.node.sourceDebug
                )
            );
        }
        return result;
    }

    private Optional<BytecodeAttribute> module() {
        final Optional<BytecodeAttribute> result;
        final ModuleNode module = this.node.module;
        if (module == null) {
            result = Optional.empty();
        } else {
            result = Optional.of(new AsmModule(module).bytecode());
        }
        return result;
    }

    private Optional<BytecodeAttribute> enclosing() {
        final Optional<BytecodeAttribute> result;
        if (this.node.outerClass == null
            && this.node.outerMethod == null
            && this.node.outerMethodDesc == null
        ) {
            result = Optional.empty();
        } else {
            result = Optional.of(
                new BytecodeAttribute.EnclosingMethod(
                    this.node.outerClass,
                    this.node.outerMethod,
                    this.node.outerMethodDesc
                )
            );
        }
        return result;
    }

    private Optional<BytecodeAttribute> nesthost() {
        final Optional<BytecodeAttribute> result;
        if (this.node.nestHostClass == null) {
            result = Optional.empty();
        } else {
            result = Optional.of(
                new BytecodeAttribute.NestHost(this.node.nestHostClass)
            );
        }
        return result;
    }

    private Optional<BytecodeAttribute> nestmembers() {
        final Optional<BytecodeAttribute> result;
        if (this.node.nestMembers == null) {
            result = Optional.empty();
        } else {
            result = Optional.of(
                new BytecodeAttribute.NestMembers(this.node.nestMembers)
            );
        }
        return result;
    }

    private Optional<BytecodeAttribute> permitted() {
        final Optional<BytecodeAttribute> result;
        if (this.node.permittedSubclasses == null) {
            result = Optional.empty();
        } else {
            result = Optional.of(
                new BytecodeAttribute.PermittedSubclasses(this.node.permittedSubclasses)
            );
        }
        return result;
    }

    private Optional<BytecodeAttribute> records() {
        final Optional<BytecodeAttribute> result;
        if (this.node.recordComponents == null) {
            result = Optional.empty();
        } else {
            result = Optional.of(
                new BytecodeAttribute.RecordComponents(
                    new AsmRecordComponents(this.node.recordComponents).bytecode()
                )
            );
        }
        return result;
    }

    private Stream<InnerClass> inners() {
        final Stream<InnerClass> result;
        if (this.node.innerClasses == null) {
            result = Stream.empty();
        } else {
            result = this.node.innerClasses.stream().map(
                clazz -> new InnerClass(
                    clazz.name,
                    clazz.outerName,
                    clazz.innerName,
                    clazz.access
                )
            );
        }
        return result;
    }
}
