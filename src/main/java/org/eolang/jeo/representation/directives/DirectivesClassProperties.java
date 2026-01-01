/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.DefaultVersion;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Class properties as Xembly directives.
 * <p>All the class directives are sorted according to JVM specification
 * {@code
 *     u2             minor_version; {@link DirectivesClassProperties}
 *     u2             major_version; {@link DirectivesClassProperties}
 *     u2             constant_pool_count; (incorporated to the directives)
 *     cp_info        constant_pool[constant_pool_count-1]; (incorporated to the directives)
 *     u2             access_flags; {@link DirectivesClassProperties}
 *     u2             this_class; {@link DirectivesClassProperties} (class name)
 *     u2             super_class;  {@link DirectivesClassProperties}
 *     u2             interfaces_count;  {@link DirectivesClassProperties}
 *     u2             interfaces[interfaces_count];  {@link DirectivesClassProperties}
 * }
 * <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.1">
 *     You can read more in the official JVM specification.
 * </a>
 * @since 0.1.0
 */
public final class DirectivesClassProperties implements Iterable<Directive> {

    /**
     * Empty interfaces array.
     */
    private static final String[] EMPTY_INTERFACES = new String[0];

    /**
     * Format of the directives.
     */
    private final Format format;

    /**
     * Class bytecode version.
     */
    private final int version;

    /**
     * Access modifiers.
     */
    private final int access;

    /**
     * Class name.
     */
    private final ClassName name;

    /**
     * Class supername.
     */
    private final String supername;

    /**
     * Class interfaces.
     */
    private final String[] interfaces;

    /**
     * Constructor.
     */
    public DirectivesClassProperties() {
        this(0);
    }

    /**
     * Constructor.
     * @param format Format of the directives.
     * @param access Access modifiers.
     */
    public DirectivesClassProperties(final Format format, final int access) {
        this(
            format,
            new DefaultVersion().bytecode(),
            access,
            new ClassName("SomeClass"),
            "",
            DirectivesClassProperties.EMPTY_INTERFACES
        );
    }

    /**
     * Constructor.
     * @param name Name of the class.
     */
    public DirectivesClassProperties(final String name) {
        this(
            new Format(),
            new DefaultVersion().bytecode(),
            0,
            new ClassName(name),
            "",
            DirectivesClassProperties.EMPTY_INTERFACES
        );
    }

    /**
     * Constructor.
     * @param access Access modifiers.
     */
    public DirectivesClassProperties(final int access) {
        this(access, "", DirectivesClassProperties.EMPTY_INTERFACES);
    }

    /**
     * Constructor.
     * @param access Access modifiers.
     * @param supername Class supername.
     * @param interfaces Class interfaces.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesClassProperties(
        final int access,
        final String supername,
        final String... interfaces
    ) {
        this(
            new Format(),
            new DefaultVersion().bytecode(),
            access,
            new ClassName("SomeClass"),
            supername,
            interfaces.clone()
        );
    }

    /**
     * Constructor.
     * @param format Format of the directives.
     * @param version Bytecode version.
     * @param access Access modifiers.
     * @param name Class name.
     * @param supername Class supername.
     * @param interfaces Class interfaces.
     * @checkstyle ParameterNumberCheck (6 lines)
     */
    public DirectivesClassProperties(
        final Format format,
        final int version,
        final int access,
        final ClassName name,
        final String supername,
        final String... interfaces
    ) {
        this.format = format;
        this.version = version;
        this.access = access;
        this.name = name;
        this.supername = supername;
        this.interfaces = interfaces.clone();
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives directives = new Directives();
        directives.append(new DirectivesValue(this.format, "version", this.version));
        directives.append(new DirectivesValue(this.format, "access", this.access));
        if (this.format.modifiers()) {
            directives.append(new DirectivesClassModifiers(this.format, this.access));
        }
        directives.append(
            new DirectivesValue(this.format, "name", this.name.full().replace('.', '/'))
        );
        if (this.supername != null) {
            directives.append(new DirectivesValue(this.format, "supername", this.supername));
        }
        if (this.interfaces != null) {
            directives.append(new DirectivesValues(this.format, "interfaces", this.interfaces));
        }
        return directives.iterator();
    }
}
