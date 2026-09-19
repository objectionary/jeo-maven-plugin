/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eolang.jeo.representation.DefaultVersion;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;

/**
 * The {@code MethodParameters} attribute of every method of a class.
 *
 * <p>{@link ClassReader#SKIP_DEBUG} drops this attribute together with line numbers and local
 * variable names, so a class read in the {@code short} mode comes back with no parameter names
 * at all and the round trip loses them. This visitor reads them back with a second pass that
 * asks neither for the code nor for the frames.</p>
 *
 * @since 0.6
 */
final class AsmParameters extends ClassVisitor {

    /**
     * Bytes of the class.
     */
    private final byte[] bytes;

    /**
     * Found parameters, keyed by the name and the descriptor of the method.
     */
    private final Map<String, List<ParameterNode>> found;

    /**
     * Constructor.
     *
     * @param bytes Bytes of the class
     */
    AsmParameters(final byte... bytes) {
        super(new DefaultVersion().api());
        this.bytes = bytes.clone();
        this.found = new HashMap<>(0);
    }

    @Override
    public MethodVisitor visitMethod(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final String[] exceptions
    ) {
        final List<ParameterNode> bag = new ArrayList<>(0);
        this.found.put(this.key(name, descriptor), bag);
        return new AsmParameter(bag);
    }

    /**
     * Put the parameters back into the methods of the node.
     *
     * @param node The node that was read without the attribute
     */
    void fill(final ClassNode node) {
        new ClassReader(this.bytes).accept(
            this, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES
        );
        for (final MethodNode method : node.methods) {
            final List<ParameterNode> bag =
                this.found.get(this.key(method.name, method.desc));
            if (method.parameters == null && bag != null && !bag.isEmpty()) {
                method.parameters = bag;
            }
        }
    }

    /**
     * The key of a method in the map of found parameters.
     *
     * @param name Name of the method
     * @param descriptor Descriptor of the method
     * @return The key
     */
    private String key(final String name, final String descriptor) {
        return String.format("%s%s", name, descriptor);
    }
}
