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
 * @todo #271:90min Split DirectivesClass into two separate classes.
 *  Currently {@link DirectivesClassVisitor} is responsible for two things:
 *  - Scanning bytecode class/
 *  - Building Xembly directives.
 *  We have to split this class into two separate classes:
 *  - DirectivesClassVisitor - responsible for scanning bytecode class.
 *  - DirectivesClass - responsible for building Xembly directives according with scanned bytecode.
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
        final ClassName clazz = new ClassName(name);
        this.directives.add("program")
            .attr("name", clazz.name())
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
            .add("metas")
            .add("meta")
            .add("head").set("package").up()
            .add("tail").set(clazz.pckg()).up()
            .add("part").set(clazz.pckg()).up()
            .up()
            .up()
            .attr("ms", System.currentTimeMillis())
            .add("objects");
        final DirectivesClassProperties props = new DirectivesClassProperties(
            access,
            signature,
            supername,
            interfaces
        );
        this.directives.add("o")
            .attr("abstract", "")
            .attr("name", clazz.name())
            .append(props);
        this.clazz.set(new DirectivesClass(clazz, props));
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
        final DirectivesMethodVisitor result;
        if (name.equals("<init>")) {
            this.directives.add("o")
                .attr("abstract", "")
                .attr("name", "new")
                .append(new DirectivesMethodProperties(access, descriptor, signature, exceptions))
                .add("o")
                .attr("base", "seq")
                .attr("name", "@");
            result = new DirectivesMethodVisitor(
                this.directives,
                super.visitMethod(access, name, descriptor, signature, exceptions)
            );
        } else {
            this.directives.add("o")
                .attr("abstract", "")
                .attr("name", name)
                .append(new DirectivesMethodProperties(access, descriptor, signature, exceptions))
                .add("o")
                .attr("base", "seq")
                .attr("name", "@");
            result = new DirectivesMethodVisitor(
                this.directives,
                super.visitMethod(access, name, descriptor, signature, exceptions)
            );
        }
        this.clazz.get().method(result);
        return result;
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
        this.directives.append(new DirectivesField(access, name, descriptor, signature, value));
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public void visitEnd() {
//        Here we should append the class to the directives.
//        this.directives.append(this.clazz.get());
        this.directives.up();
        super.visitEnd();
    }

    @Override
    public Iterator<Directive> iterator() {
        return this.directives.iterator();
    }
}
