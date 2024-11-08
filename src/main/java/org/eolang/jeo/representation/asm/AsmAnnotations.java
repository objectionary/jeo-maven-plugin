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
package org.eolang.jeo.representation.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Asm annotations.
 * Asm parser for annotations.
 * @since 0.6
 */
public final class AsmAnnotations {

    /**
     * Visible annotations.
     */
    private final List<AnnotationNode> visible;

    /**
     * Invisible annotations.
     */
    private final List<AnnotationNode> invisible;

    /**
     * Constructor.
     * @param node Class node.
     */
    AsmAnnotations(final ClassNode node) {
        this(node.visibleAnnotations, node.invisibleAnnotations);
    }

    /**
     * Constructor.
     * @param node Method node.
     */
    AsmAnnotations(final MethodNode node) {
        this(node.visibleAnnotations, node.invisibleAnnotations);
    }

    /**
     * Constructor.
     * @param node Field node.
     */
    AsmAnnotations(final FieldNode node) {
        this(node.visibleAnnotations, node.invisibleAnnotations);
    }

    /**
     * Constructor.
     * @param visible Visible annotations.
     * @param invisible Invisible annotations.
     */
    AsmAnnotations(
        final List<AnnotationNode> visible,
        final List<AnnotationNode> invisible
    ) {
        this.visible = visible;
        this.invisible = invisible;
    }

    /**
     * Convert asm annotations to domain annotations.
     * @return Domain annotations.
     */
    public BytecodeAnnotations bytecode() {
        return new BytecodeAnnotations(
            Stream.concat(
                AsmAnnotations.safe(this.visible, true),
                AsmAnnotations.safe(this.invisible, false)
            ).collect(Collectors.toList())
        );
    }

    /**
     * Safe annotations.
     * @param nodes Annotation nodes.
     * @param visible Is it visible?
     * @return Annotations.
     */
    private static Stream<BytecodeAnnotation> safe(
        final List<AnnotationNode> nodes, final boolean visible
    ) {
        return Optional.ofNullable(nodes)
            .orElse(new ArrayList<>(0))
            .stream()
            .map(ann -> AsmAnnotations.annotation(ann, visible));
    }

    /**
     * Convert asm annotation to domain annotation.
     * @param node Asm annotation node.
     * @param visible Is it visible?
     * @return Domain annotation.
     */
    private static BytecodeAnnotation annotation(final AnnotationNode node, final boolean visible) {
        final List<BytecodeAnnotationValue> properties = new ArrayList<>(0);
        final List<Object> values = Optional.ofNullable(node.values).orElse(new ArrayList<>(0));
        for (int index = 0; index < values.size(); index += 2) {
            properties.add(
                new AsmAnnotationProperty(
                    (String) values.get(index),
                    values.get(index + 1)
                ).bytecode()
            );
        }
        return new BytecodeAnnotation(node.desc, visible, properties);
    }
}
