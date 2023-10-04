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
package org.eolang.jeo.representation.asm;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
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
 * @todo #84:30min Handle constructors in classes.
 *  Right now we just skip constructors. We should handle them in order to
 *  build correct XML representation of the class. When the method is ready
 *  remove that puzzle.
 */
@SuppressWarnings({"PMD.UseObjectForClearerAPI", "PMD.AvoidDuplicateLiterals"})
public final class DirectivesClass extends ClassVisitor implements Iterable<Directive> {

    /**
     * Bytecode listing.
     */
    private final String listing;

    /**
     * Xembly directives.
     */
    private final Directives directives;

    /**
     * Constructor.
     * @param listing Bytecode listing.
     */
    public DirectivesClass(final String listing) {
        this(new DefaultVersion().api(), new Directives(), listing);
    }

    /**
     * Constructor.
     */
    DirectivesClass() {
        this("");
    }

    /**
     * Constructor.
     * @param api ASM API version.
     * @param directives Xembly directives.
     * @param listing Bytecode listing.
     */
    private DirectivesClass(
        final int api,
        final Directives directives,
        final String listing
    ) {
        super(api);
        this.directives = directives;
        this.listing = listing;
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
        this.directives.add("program")
            .attr("name", name.replace('/', '.'))
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
            .add("metas").up()
            .attr("ms", System.currentTimeMillis())
            .add("objects");
        this.directives.add("o")
            .attr("abstract", "")
            .attr("name", DirectivesClass.className(access, name));
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
        final MethodVisitor result;
        if (name.equals("<init>")) {
            result = super.visitMethod(access, name, descriptor, signature, exceptions);
        } else {
            this.directives.add("o")
                .attr("abstract", "")
                .attr("name", DirectivesClass.methodName(access, name, descriptor));
            if (Type.getMethodType(descriptor).getArgumentTypes().length > 0) {
                this.directives.add("o")
                    .attr("name", "args")
                    .up();
            }
            this.directives.add("o")
                .attr("base", "seq")
                .attr("name", "@");
            result = new MethodDirectives(
                this.directives,
                super.visitMethod(access, name, descriptor, signature, exceptions)
            );
        }
        return result;
    }

    @Override
    public void visitEnd() {
        this.directives.up();
        super.visitEnd();
    }

    @Override
    public Iterator<Directive> iterator() {
        return this.directives.iterator();
    }

    /**
     * Class name.
     * @param access Access.
     * @param name Name.
     * @return Class name.
     * @todo #108:90min Implement different way to generate class name.
     *  Right now we use class name and access to generate class name and insert it as a name
     *  attribute of the class. This is not a good way to do it. At least it looks ugly.
     *  We should find a better way to generate class name and place it somewhere in the XML
     *  representation of the class.
     */
    private static String className(final int access, final String name) {
        return String.format("%d__%s", access, name);
    }

    /**
     * Method name.
     * @param access Access.
     * @param name Name.
     * @param descriptor Descriptor.
     * @return Method name.
     * @todo #108:90min Implement different way to generate method name.
     *  Right now we use method name, access and descriptor to generate method name
     *  and insert it as a name attribute of the method. This is not a good way to do it.
     *  At least it looks ugly. We should find a better way to generate method name and place
     *  it somewhere in the XML representation of the class.
     */
    private static String methodName(final int access, final String name, final String descriptor) {
        return String.format(
            "%d__%s__%s",
            access,
            name,
            descriptor
        );
    }
}
