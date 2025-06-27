/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
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
import org.eolang.jeo.representation.directives.DirectivesObject;

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
     * Convert to directives.
     * @param listing Program listing.
     * @return Directives program.
     */
    public DirectivesObject directives(final String listing) {
        final BytecodeClass top = this.top();
        final ClassName classname = new ClassName(this.pckg, new PrefixedName(top.name()).encode());
        final DirectivesClass clazz = top.directives();
        return new DirectivesObject(
            listing,
            clazz,
            new DirectivesMetas(classname)
        );
    }
}
