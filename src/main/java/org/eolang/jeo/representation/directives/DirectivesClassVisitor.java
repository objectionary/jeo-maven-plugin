/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.DefaultVersion;
import org.eolang.jeo.representation.JavaName;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Class printer.
 * ASM class visitor which scans the class and builds Xembly directives.
 * You can read more about Xembly right here:
 * - https://github.com/yegor256/xembly
 * - https://www.xembly.org
 * Firther all this directives will be used to build XML representation of the class.
 * @since 0.1
 * @todo #107:30min Change method argument naming strategy.
 *  Right now we use method argument type and index to generate method argument name.
 *  For example for method with signature `void foo(int a, String b)` we generate
 *  method argument names `arg__I__0` and `arg__Ljava/lang/String__1`. This is not a good way
 *  to do it. At least it leads to errors when argument type is an array or class.
 *  So we have to test this cases and maybe create a better strategy for arguments naming.
 */
@SuppressWarnings({"PMD.UseObjectForClearerAPI", "PMD.AvoidDuplicateLiterals"})
public final class DirectivesClassVisitor extends ClassVisitor implements Iterable<Directive> {

    /**
     * Program directives.
     */
    private final DirectivesProgram program;

    /**
     * Opcodes counting.
     */
    private final boolean counting;

    /**
     * Constructor.
     */
    DirectivesClassVisitor() {
        this("");
    }

    /**
     * Constructor.
     * @param listing Bytecode listing.
     */
    public DirectivesClassVisitor(final String listing) {
        this(new DefaultVersion().api(), listing);
    }

    /**
     * Constructor.
     * @param listing Bytecode listing.
     * @param counting Opcodes counting.
     */
    public DirectivesClassVisitor(final String listing, final boolean counting) {
        this(new DefaultVersion().api(), listing, counting);
    }

    /**
     * Constructor.
     * @param api ASM API version.
     * @param program Program directives.
     */
    public DirectivesClassVisitor(final int api, final String program) {
        this(api, program, true);
    }

    /**
     * Constructor.
     * @param api ASM API version.
     * @param listing Bytecode listing.
     * @param counting Opcodes counting.
     */
    private DirectivesClassVisitor(
        final int api,
        final String listing,
        final boolean counting
    ) {
        this(api, new DirectivesProgram(listing), counting);
    }

    /**
     * Constructor.
     * @param api ASM API version.
     * @param program Program directives.
     * @param counting Opcodes counting.
     */
    public DirectivesClassVisitor(
        final int api,
        final DirectivesProgram program,
        final boolean counting
    ) {
        super(api);
        this.program = program;
        this.counting = counting;
    }

    @Override
    public void visit(
        final int version,
        final int access,
        final String name,
        final String signature,
        final String supername,
        final String[] interfaces
    ) {
        final ClassName classname = new ClassName(new JavaName(name).encode());
        this.program.withClass(
            classname,
            new DirectivesClass(
                classname,
                new DirectivesClassProperties(
                    version,
                    access,
                    signature,
                    supername,
                    interfaces
                )
            )
        );
        super.visit(version, access, name, signature, supername, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final String[] exceptions
    ) {
        final String ename = new JavaName(name).encode();
        final DirectivesMethod method = new DirectivesMethod(
            ename,
            this.counting,
            new DirectivesMethodProperties(access, descriptor, signature, exceptions)
        );
        this.program.top().method(method);
        return new DirectivesMethodVisitor(
            method,
            super.visitMethod(access, ename, descriptor, signature, exceptions)
        );
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        final DirectivesAnnotation annotation = new DirectivesAnnotation(descriptor, visible);
        this.program.top().annotation(annotation);
        return new DirectivesAnnotationVisitor(
            this.api,
            super.visitAnnotation(descriptor, visible),
            annotation
        );
    }

    @Override
    public FieldVisitor visitField(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final Object value
    ) {
        final String ename = new JavaName(name).encode();
        final DirectivesField field = new DirectivesField(
            access,
            ename,
            descriptor,
            signature,
            value
        );
        this.program.top().field(field);
        return new DirectivesFieldVisitor(
            super.visitField(access, ename, descriptor, signature, value),
            field
        );
    }

    @Override
    public Iterator<Directive> iterator() {
        return this.program.iterator();
    }
}
