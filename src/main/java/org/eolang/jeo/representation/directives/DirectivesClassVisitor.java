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

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.DefaultVersion;
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
     * Bytecode listing.
     */
    private final String listing;

    /**
     * Xembly directives.
     */
    private final Directives directives;

    /**
     * Class directives.
     * This field uses atomic reference because the field can't be initialized in the constructor.
     * It is ASM framework limitation.
     */
    private final AtomicReference<DirectivesClass> clazz;

    /**
     * Constructor.
     * @param listing Bytecode listing.
     */
    public DirectivesClassVisitor(final String listing) {
        this(new DefaultVersion().api(), new Directives(), listing);
    }

    /**
     * Constructor.
     */
    DirectivesClassVisitor() {
        this("");
    }

    /**
     * Constructor.
     * @param api ASM API version.
     * @param directives Xembly directives.
     * @param listing Bytecode listing.
     */
    private DirectivesClassVisitor(
        final int api,
        final Directives directives,
        final String listing
    ) {
        super(api);
        this.directives = directives;
        this.listing = listing;
        this.clazz = new AtomicReference<>();
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
        final String now = ZonedDateTime.now(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT);
        final ClassName classname = new ClassName(name);
        this.directives.add("program")
            .attr("name", classname.name())
            .attr("version", "0.0.0")
            .attr("revision", "0.0.0")
            .attr("dob", now)
            .attr("time", now)
            .add("listing")
            .set(this.listing)
            .up()
            .add("errors").up()
            .add("sheets").up()
            .add("license").up()
            .append(new DirectivesMetas(classname))
            .attr("ms", System.currentTimeMillis())
            .add("objects");
        final DirectivesClassProperties props = new DirectivesClassProperties(
            access,
            signature,
            supername,
            interfaces
        );
        this.clazz.set(new DirectivesClass(classname, props));
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
        final DirectivesMethod method = new DirectivesMethod(
            name,
            new DirectivesMethodProperties(access, descriptor, signature, exceptions)
        );
        this.clazz.get().method(method);
        return new DirectivesMethodVisitor(
            method,
            super.visitMethod(access, name, descriptor, signature, exceptions)
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
        this.clazz.get().field(new DirectivesField(access, name, descriptor, signature, value));
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public void visitEnd() {
        this.directives.append(this.clazz.get());
        this.directives.up();
        super.visitEnd();
    }

    @Override
    public Iterator<Directive> iterator() {
        return this.directives.iterator();
    }
}
