/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eolang.jeo.representation.bytecode.BytecodeModule;
import org.eolang.jeo.representation.bytecode.BytecodeModuleExported;
import org.eolang.jeo.representation.bytecode.BytecodeModuleOpened;
import org.eolang.jeo.representation.bytecode.BytecodeModuleProvided;
import org.eolang.jeo.representation.bytecode.BytecodeModuleRequired;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.ModuleExportNode;
import org.objectweb.asm.tree.ModuleNode;
import org.objectweb.asm.tree.ModuleOpenNode;
import org.objectweb.asm.tree.ModuleProvideNode;
import org.objectweb.asm.tree.ModuleRequireNode;

/**
 * Test case for {@link AsmModule}.
 * @since 0.15.0
 */
final class AsmModuleTest {

    @Test
    void convertsSimpleModuleToDomainBytecode() {
        final String name = "name";
        final int access = 0;
        final String version = "0.1.0";
        final ModuleNode node = new ModuleNode(name, access, version);
        final String main = "main";
        node.mainClass = main;
        final BytecodeModule bytecode = new AsmModule(node).bytecode();
        MatcherAssert.assertThat(
            "We expect the module to be converted to domain bytecode correctly",
            bytecode,
            org.hamcrest.Matchers.equalTo(
                new BytecodeModule(
                    name,
                    access,
                    version,
                    main,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList()
                )
            )
        );
    }

    @Test
    void convertsModuleWithPackagesToDomainBytecode() {
        final String name = "name";
        final int access = 0;
        final String version = "0.1.0";
        final ModuleNode node = new ModuleNode(
            name,
            access,
            version
        );
        final String main = "main";
        node.mainClass = main;
        node.packages = new ArrayList<>(2);
        final String example = "com.example";
        final String sample = "org.sample";
        node.packages.add(example);
        node.packages.add(sample);
        final BytecodeModule bytecode = new AsmModule(node).bytecode();
        MatcherAssert.assertThat(
            "We expect the module with packages to be converted to domain bytecode correctly",
            bytecode,
            org.hamcrest.Matchers.equalTo(
                new BytecodeModule(
                    name,
                    access,
                    version,
                    main,
                    Arrays.asList(example, sample),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList()
                )
            )
        );
    }

    @Test
    void convertsModuleWithEverythingToDomainBytecode() {
        final String name = "name";
        final int access = 0;
        final String version = "0.1.0";
        final ModuleNode node = new ModuleNode(
            name,
            access,
            version
        );
        final String main = "main";
        node.mainClass = main;
        node.packages = new ArrayList<>(2);
        final String example = "com.example";
        final String sample = "org.sample";
        node.packages.add(example);
        node.packages.add(sample);
        node.requires = new ArrayList<>(1);
        node.requires.add(new ModuleRequireNode("required", 0, "0.1.1"));
        node.exports = new ArrayList<>(1);
        final List<String> emodules = Collections.singletonList("m1");
        node.exports.add(new ModuleExportNode("exported", 0, emodules));
        node.opens = new ArrayList<>(1);
        final List<String> omodules = Collections.singletonList("m2");
        node.opens.add(new ModuleOpenNode("opened", 0, omodules));
        node.provides = new ArrayList<>(1);
        final List<String> impls = new ArrayList<>(1);
        impls.add("impl");
        node.provides.add(new ModuleProvideNode("service", impls));
        node.uses = new ArrayList<>(1);
        node.uses.add("used");
        final BytecodeModule bytecode = new AsmModule(node).bytecode();
        MatcherAssert.assertThat(
            "We expect the module with everything to be converted to domain bytecode correctly",
            bytecode,
            org.hamcrest.Matchers.equalTo(
                new BytecodeModule(
                    name,
                    access,
                    version,
                    main,
                    Arrays.asList(example, sample),
                    Collections.singletonList(
                        new BytecodeModuleRequired("required", 0, "0.1.1")
                    ),
                    Collections.singletonList(
                        new BytecodeModuleExported("exported", 0, emodules)
                    ),
                    Collections.singletonList(
                        new BytecodeModuleOpened("opened", 0, omodules)
                    ),
                    Collections.singletonList(
                        new BytecodeModuleProvided("service", impls)
                    ),
                    Collections.singletonList("used")
                )
            )
        );
    }
}
