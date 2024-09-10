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
package org.eolang.jeo.representation.bytecode;

import com.jcabi.xml.XML;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.PluginStartup;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.directives.DirectivesMetas;
import org.eolang.jeo.representation.directives.DirectivesProgram;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;
import org.xembly.Directive;

/**
 * Bytecode program.
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
public final class BytecodeProgram {
    /**
     * Package.
     */
    private final String pckg;

    /**
     * Class.
     */
    private final List<BytecodeClass> classes;

    /**
     * Constructor.
     * @param pckg Package.
     */
    public BytecodeProgram(final String pckg) {
        this(pckg, new ArrayList<>(0));
    }

    /**
     * Constructor.
     * @param classes Classes.
     */
    public BytecodeProgram(final BytecodeClass... classes) {
        this("", Arrays.asList(classes));
    }

    /**
     * Constructor.
     * @param pckg Package.
     * @param classes Classes.
     */
    public BytecodeProgram(final String pckg, final BytecodeClass... classes) {
        this(pckg, Arrays.asList(classes));
    }

    /**
     * Constructor.
     * @param pckg Package.
     * @param classes Classes.
     */
    public BytecodeProgram(final String pckg, final List<BytecodeClass> classes) {
        this.pckg = pckg;
        this.classes = classes;
    }

    /**
     * Converts bytecode into XML.
     *
     * @return XML representation of bytecode.
     */
    public XML xml() {
        return new BytecodeRepresentation(this.bytecode()).toEO();
    }

    /**
     * Generate bytecode.
     * <p>
     * In this method we intentionally use the Thread.currentThread().getContextClassLoader()
     * for the class loader of the
     * {@link CheckClassAdapter#verify(ClassReader, ClassLoader, boolean, PrintWriter)}
     * instead of default implementation. This is because the default class loader doesn't
     * know about classes compiled on the previous maven step.
     * You can read more about the problem here:
     * {@link PluginStartup#init()} ()}
     * </p>
     * @return Bytecode.
     */
    public Bytecode bytecode() {
        return this.bytecode(true);
    }

    /**
     * Traverse XML and build bytecode class.
     * @param verify Verify bytecode.
     * @return Bytecode.
     */
    public Bytecode bytecode(final boolean verify) {
        final CustomClassWriter writer = new CustomClassWriter(verify);
        this.top().writeTo(writer, this.pckg);
        return writer.bytecode();
    }

    /**
     * Get top class.
     * @return Top class.
     */
    public BytecodeClass top() {
        return this.classes.get(0);
    }

    /**
     * Copy program with replaced top class.
     *
     * @param clazz Class to replace.
     * @return Program with replaced top class.
     */
    public BytecodeProgram replaceTopClass(final BytecodeClass clazz) {
        return new BytecodeProgram(
            this.pckg,
            new ArrayList<>(Collections.singletonList(clazz))
        );
    }

    /**
     * Copy program without top class.
     *
     * @return Program without top class.
     */
    public BytecodeProgram withoutTopClass() {
        return new BytecodeProgram(
            this.pckg,
            new ArrayList<>(Collections.emptyList())
        );
    }


    public DirectivesProgram directives(final String code) {
        //todo: metas
        DirectivesMetas metas = new DirectivesMetas();
        return new DirectivesProgram(
            code,
            this.top().directives(),
            metas
        );
    }
}
