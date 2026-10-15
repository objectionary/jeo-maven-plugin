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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.TypePath;

/**
 * Ownership of constructor lists in bytecode models.
 *
 * @since 0.0.0
 */
final class BytecodeCollectionsSnapshotTest {

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

    private static <T> Arguments sample(
        final String label, final Function<List<T>, Object> factory, final T element
    ) {
        final List<T> values = new ArrayList<>(Collections.singleton(element));
        return Arguments.of(label, factory.apply(values), (Runnable) values::clear);
    }

    private static Stream<Arguments> models() {
        return Stream.of(
            sample(
                "annotations",
                BytecodeAnnotations::new,
                new BytecodeAnnotation("LExample;", true)
            ),
            BytecodeCollectionsSnapshotTest.<BytecodeAttribute>sample(
                "attributes",
                BytecodeAttributes::new,
                new BytecodeAttribute.NestHost("Host")
            ),
            sample(
                "type annotations",
                BytecodeTypeAnnotations::new,
                new BytecodeTypeAnnotation(
                    0,
                    TypePath.fromString(""),
                    "LExample;",
                    true,
                    Collections.emptyList()
                )
            ),
            sample("nest members", BytecodeAttribute.NestMembers::new, "Host$Member"),
            sample("permitted subclasses", BytecodeAttribute.PermittedSubclasses::new, "Child"),
            sample(
                "record components",
                BytecodeAttribute.RecordComponents::new,
                new BytecodeRecordComponent("value", "I", null)
            )
        );
    }
}
