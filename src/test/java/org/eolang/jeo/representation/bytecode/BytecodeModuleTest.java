/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Collections;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.representation.asm.AsmModule;
import org.eolang.jeo.representation.asm.AsmProgram;
import org.eolang.jeo.representation.directives.DirectivesModule;
import org.eolang.jeo.representation.directives.DirectivesModuleExported;
import org.eolang.jeo.representation.directives.DirectivesModuleOpened;
import org.eolang.jeo.representation.directives.DirectivesModuleProvided;
import org.eolang.jeo.representation.directives.DirectivesModuleRequired;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.ClassNode;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link BytecodeModule}.
 * @since 0.15.0
 */
final class BytecodeModuleTest {

    @Test
    void writesModuleToAsmClass() {
        final String version = "0.1.1";
        final BytecodeModule original = new BytecodeModule(
            "name",
            0,
            version,
            "main",
            Collections.singletonList("org.eolang.jeo"),
            Collections.singletonList(new BytecodeModuleRequired("required", 0, version)),
            Collections.singletonList(
                new BytecodeModuleExported("exported", 1, Collections.singletonList("m1"))
            ),
            Collections.singletonList(
                new BytecodeModuleOpened("opened", 2, Collections.singletonList("m2"))
            ),
            Collections.singletonList(
                new BytecodeModuleProvided("provided", Collections.singletonList("impl"))
            ),
            Collections.singletonList("used")
        );
        final ClassNode node = new ClassNode();
        original.write(node);
        MatcherAssert.assertThat(
            "We expect the module to be written to ASM class and read back correctly",
            new AsmModule(node.module).bytecode(),
            Matchers.equalTo(original)
        );
    }

    @Test
    void disassemblesOpenModuleAndAssemblesItBack() throws Exception {
        final BytecodeObject domain = new AsmProgram(
            new Bytecode(new BytesOf(new ResourceOf("open-module-info.class")).asBytes()).bytes()
        ).bytecode();
        final Bytecode second = domain.bytecode();
        MatcherAssert.assertThat(
            "We expect to read the same bytecode after writing it to ASM and reading it back",
            new AsmProgram(second.bytes()).bytecode(),
            Matchers.equalTo(domain)
        );
    }

    @Test
    void convertsToDirectives() throws ImpossibleModificationException {
        final Format format = new Format();
        final String version = "1.1.1";
        final String actual = new Xembler(
            new DirectivesModule(
                format,
                "name",
                0,
                version,
                "main",
                Collections.singletonList("org.eolang.jeo"),
                Collections.singletonList(
                    new DirectivesModuleRequired(format, "required", 0, version)
                ),
                Collections.singletonList(
                    new DirectivesModuleExported(
                        format, "exported", 1, Collections.singletonList("m1")
                    )
                ),
                Collections.singletonList(
                    new DirectivesModuleOpened(format, "opened", 2, Collections.singletonList("m2"))
                ),
                Collections.singletonList(
                    new DirectivesModuleProvided(
                        format, "provided", Collections.singletonList("impl")
                    )
                ),
                Collections.singletonList("used")
            )
        ).xml();
        MatcherAssert.assertThat(
            "We expect to receive the same XML representation",
            new Xembler(
                new BytecodeModule(
                    "name",
                    0,
                    version,
                    "main",
                    Collections.singletonList("org.eolang.jeo"),
                    Collections.singletonList(new BytecodeModuleRequired("required", 0, version)),
                    Collections.singletonList(
                        new BytecodeModuleExported("exported", 1, Collections.singletonList("m1"))
                    ),
                    Collections.singletonList(
                        new BytecodeModuleOpened("opened", 2, Collections.singletonList("m2"))
                    ),
                    Collections.singletonList(
                        new BytecodeModuleProvided("provided", Collections.singletonList("impl"))
                    ),
                    Collections.singletonList("used")
                ).directives(0, format)
            ).xml(),
            Matchers.equalTo(actual)
        );
    }
}
