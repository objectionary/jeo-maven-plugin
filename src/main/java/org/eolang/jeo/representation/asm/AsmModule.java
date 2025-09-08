/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeModule;
import org.eolang.jeo.representation.bytecode.BytecodeModuleExported;
import org.eolang.jeo.representation.bytecode.BytecodeModuleOpened;
import org.eolang.jeo.representation.bytecode.BytecodeModuleProvided;
import org.eolang.jeo.representation.bytecode.BytecodeModuleRequired;
import org.objectweb.asm.tree.ModuleNode;

/**
 * ASM-based bytecode parser for Java modules.
 * @since 0.15.0
 */
public final class AsmModule {

    /**
     * Module node.
     */
    private final ModuleNode module;

    /**
     * Constructor.
     * @param node The ASM module node to parse.
     */
    public AsmModule(final ModuleNode node) {
        this.module = node;
    }

    /**
     * Convert ASM module to domain bytecode module.
     * @return The domain bytecode module representation.
     */
    public BytecodeModule bytecode() {
        return new BytecodeModule(
            this.module.name,
            this.module.access,
            this.module.version,
            this.module.mainClass,
            this.module.packages,
            AsmModule.requires(this.module),
            AsmModule.exports(this.module),
            AsmModule.opens(this.module),
            AsmModule.provides(this.module),
            this.module.uses
        );
    }

    /**
     * Module provides.
     * @param module ASM module node.
     * @return List of module provides.
     */
    private static List<BytecodeModuleProvided> provides(final ModuleNode module) {
        return Optional.ofNullable(module.provides).orElse(Collections.emptyList())
            .stream()
            .map(prov -> new BytecodeModuleProvided(prov.service, prov.providers))
            .collect(Collectors.toList());
    }

    /**
     * Module opens.
     * @param module ASM module node.
     * @return List of module opens.
     */
    private static List<BytecodeModuleOpened> opens(final ModuleNode module) {
        return Optional.ofNullable(module.opens).orElse(Collections.emptyList())
            .stream()
            .map(opn -> new BytecodeModuleOpened(opn.packaze, opn.access, opn.modules))
            .collect(Collectors.toList());
    }

    /**
     * Module exports.
     * @param module ASM module node.
     * @return List of module exports.
     */
    private static List<BytecodeModuleExported> exports(final ModuleNode module) {
        return Optional.ofNullable(module.exports).orElse(Collections.emptyList())
            .stream()
            .map(exp -> new BytecodeModuleExported(exp.packaze, exp.access, exp.modules))
            .collect(Collectors.toList());
    }

    /**
     * Module requires.
     * @param module ASM module node.
     * @return List of module requirements.
     */
    private static List<BytecodeModuleRequired> requires(final ModuleNode module) {
        return Optional.ofNullable(module.requires).orElse(Collections.emptyList())
            .stream()
            .map(req -> new BytecodeModuleRequired(req.module, req.access, req.version))
            .collect(Collectors.toList());
    }
}
