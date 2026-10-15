/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.representation.ClassName;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link BytecodeModule}.
 *
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
        MatcherAssert.assertThat(
            "We expect to read the same bytecode after writing it to ASM and reading it back",
            new AsmProgram(domain.bytecode().bytes()).bytecode(),
            Matchers.equalTo(domain)
        );
    }

    @Test
    void convertsToDirectives() throws ImpossibleModificationException {
        final Format format = new Format();
        final String version = "1.1.1";
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
            Matchers.equalTo(
                new Xembler(
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
                            new DirectivesModuleOpened(
                                format, "opened", 2, Collections.singletonList("m2")
                            )
                        ),
                        Collections.singletonList(
                            new DirectivesModuleProvided(
                                format, "provided", Collections.singletonList("impl")
                            )
                        ),
                        Collections.singletonList("used")
                    )
                ).xml()
            )
        );
    }

    @ParameterizedTest
    @MethodSource("models")
    void staysUsableAsAMapKeyAfterTheInputChanges(
        final String label, final Object model, final Runnable change
    ) {
        final Map<Object, String> cache = new HashMap<>();
        cache.put(model, "original");
        change.run();
        MatcherAssert.assertThat(
            String.format("Changing the caller's list corrupted the %s map key", label),
            cache.get(model), Matchers.equalTo("original")
        );
    }

    private static Stream<Arguments> models() {
        return Snapshots.models();
    }

    /**
     * Constructor cases across the bytecode model families.
     *
     * @since 0.0.0
     */
    private static final class Snapshots {

        private static <T> Arguments sample(
            final String label, final Function<List<T>, Object> factory, final T element
        ) {
            final List<T> values = new ArrayList<>(Collections.singleton(element));
            return Arguments.of(label, factory.apply(values), (Runnable) values::clear);
        }

        private static Stream<Arguments> models() {
            return Stream.of(
                classModels(), methodModels(), parameterModels(), moduleModels(), serviceModels()
            ).flatMap(Function.identity());
        }

        private static Stream<Arguments> classModels() {
            return Stream.of(
                sample(
                    "class methods",
                    v -> new BytecodeClass("Example", v, new BytecodeClassProperties(0)),
                    new BytecodeMethod("method")
                ),
                sample(
                    "class fields",
                    v -> new BytecodeClass(
                        new ClassName("Example"),
                        Collections.emptyList(),
                        v,
                        new BytecodeAnnotations(),
                        new BytecodeAttributes(),
                        new BytecodeClassProperties(0)
                    ),
                    new BytecodeField("value", "I", null, null, 0)
                )
            );
        }

        private static Stream<Arguments> methodModels() {
            return Stream.of(
                Snapshots.<BytecodeEntry>sample(
                    "method try blocks",
                    v -> new BytecodeMethod(
                        v,
                        Collections.emptyList(),
                        new BytecodeAnnotations(),
                        new BytecodeMethodProperties("method", "()V"),
                        Collections.emptyList(),
                        new BytecodeMaxs(),
                        new BytecodeAttributes()
                    ),
                    new BytecodeTryCatchBlock("start", "end", "handler", "java/lang/Exception")
                ),
                Snapshots.<BytecodeEntry>sample(
                    "method instructions",
                    v -> new BytecodeMethod(
                        Collections.emptyList(),
                        v,
                        new BytecodeAnnotations(),
                        new BytecodeMethodProperties("method", "()V"),
                        Collections.emptyList(),
                        new BytecodeMaxs(),
                        new BytecodeAttributes()
                    ),
                    new BytecodeInstruction(Opcodes.RETURN)
                ),
                sample(
                    "method default values",
                    v -> new BytecodeMethod(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        new BytecodeAnnotations(),
                        new BytecodeMethodProperties("method", "()V"),
                        v,
                        new BytecodeMaxs(),
                        new BytecodeAttributes()
                    ),
                    new BytecodeDefaultValue(new BytecodePlainAnnotationValue("answer", 42))
                )
            );
        }

        private static Stream<Arguments> parameterModels() {
            return Stream.of(
                sample(
                    "method parameters",
                    BytecodeMethodParameters::new,
                    new BytecodeMethodParameter(0, Type.INT_TYPE)
                ),
                sample(
                    "parameter annotations",
                    v -> new BytecodeMethodParameters(Collections.emptyList(), v),
                    new BytecodeParamAnnotations(0, new BytecodeAnnotations())
                )
            );
        }

        private static Stream<Arguments> moduleModels() {
            return Stream.of(
                sample(
                    "module packages",
                    v -> new BytecodeModule(
                        "example",
                        0,
                        null,
                        null,
                        v,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                    ),
                    "example/pkg"
                ),
                sample(
                    "module requires",
                    v -> new BytecodeModule(
                        "example",
                        0,
                        null,
                        null,
                        Collections.emptyList(),
                        v,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                    ),
                    new BytecodeModuleRequired("java.base", 0, null)
                ),
                sample(
                    "module exports",
                    v -> new BytecodeModule(
                        "example",
                        0,
                        null,
                        null,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        v,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                    ),
                    new BytecodeModuleExported("example/pkg", 0, Collections.emptyList())
                ),
                sample(
                    "module opens",
                    v -> new BytecodeModule(
                        "example",
                        0,
                        null,
                        null,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        v,
                        Collections.emptyList(),
                        Collections.emptyList()
                    ),
                    new BytecodeModuleOpened("example/pkg", 0, Collections.emptyList())
                ),
                sample(
                    "module provides",
                    v -> new BytecodeModule(
                        "example",
                        0,
                        null,
                        null,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        v,
                        Collections.emptyList()
                    ),
                    new BytecodeModuleProvided("Service", Collections.singletonList("Provider"))
                ),
                sample(
                    "module uses",
                    v -> new BytecodeModule(
                        "example",
                        0,
                        null,
                        null,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        v
                    ),
                    "Service"
                )
            );
        }

        private static Stream<Arguments> serviceModels() {
            return Stream.of(
                sample(
                    "exported modules",
                    v -> new BytecodeModuleExported("example/pkg", 0, v),
                    "consumer"
                ),
                sample(
                    "opened modules",
                    v -> new BytecodeModuleOpened("example/pkg", 0, v),
                    "consumer"
                ),
                sample(
                    "service providers",
                    v -> new BytecodeModuleProvided("Service", v),
                    "Provider"
                )
            );
        }
    }
}
