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
package org.eolang.jeo.representation.bytecode;

import com.jcabi.xml.XML;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.PrefixedName;
import org.eolang.jeo.representation.directives.DirectivesClass;
import org.eolang.jeo.representation.directives.DirectivesMetas;
import org.eolang.jeo.representation.directives.DirectivesProgram;

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
     * Traverse XML and build bytecode class.
     * @return Bytecode.
     */
    public Bytecode bytecode() {
        final CustomClassWriter writer = new CustomClassWriter();
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

    /**
     * Convert to directives.
     * @param listing Program listing.
     * @return Directives program.
     */
    public DirectivesProgram directives(final String listing) {
        final BytecodeClass top = this.top();
        final ClassName classname = new ClassName(this.pckg, new PrefixedName(top.name()).encode());
        final DirectivesClass clazz = top.directives();
        return new DirectivesProgram(
            listing,
            clazz,
            new DirectivesMetas(classname)
        );
    }
}
