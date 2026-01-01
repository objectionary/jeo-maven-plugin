/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.lines;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import java.io.IOException;
import java.io.InputStream;

public class Lines {

    public static void checkTransformationPreserveLines() throws IOException {
        LineCountVisitor visitor = new LineCountVisitor();
        try (InputStream in = Dummy.class.getResourceAsStream("Dummy.class")) {
            if (in == null) {
                throw new IOException("Dummy.class resource not found via Dummy.class loader");
            }
            System.out.println(
                "Using Dummy.class loader to read the class bytes for transformation check."
            );
            new ClassReader(in).accept(visitor, 0);
        }
        int lines = visitor.total();
        if (lines <= 0) {
            throw new RuntimeException(
                String.format(
                    "We expect that line numbers are present in bytecode, but got %d instead.", lines
                )
            );
        }
    }

    private static final class LineCountVisitor extends ClassVisitor {

        /**
         * Count.
         */
        private int count;

        /**
         * Ctor.
         */
        LineCountVisitor() {
            super(Opcodes.ASM9);
        }

        @Override
        public MethodVisitor visitMethod(
            final int access,
            final String name,
            final String descriptor,
            final String signature,
            final String[] exceptions
        ) {
            return new MethodVisitor(Opcodes.ASM9) {
                @Override
                public void visitLineNumber(final int line, final Label start) {
                    LineCountVisitor.this.count += 1;
                }
            };
        }

        /**
         * Total found.
         * @return Lines count
         */
        public int total() {
            return this.count;
        }
    }
}
