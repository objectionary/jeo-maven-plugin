/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import java.util.Optional;
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
     * Class Signature.
     */
    private final String signature;

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
     * @param access Access modifiers.
     */
    public DirectivesClassProperties(final int access) {
        this(access, "");
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
            "",
            "",
            DirectivesClassProperties.EMPTY_INTERFACES
        );
    }

    /**
     * Constructor.
     * @param access Access modifiers.
     * @param signature Class Signature.
     */
    public DirectivesClassProperties(final int access, final String signature) {
        this(access, signature, "", DirectivesClassProperties.EMPTY_INTERFACES);
    }

    /**
     * Constructor.
     * @param access Access modifiers.
     * @param signature Class Signature.
     * @param supername Class supername.
     * @param interfaces Class interfaces.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesClassProperties(
        final int access,
        final String signature,
        final String supername,
        final String... interfaces
    ) {
        this(
            new Format(),
            new DefaultVersion().bytecode(),
            access,
            signature,
            supername,
            interfaces.clone()
        );
    }

    /**
     * Constructor.
     * @param format Format of the directives.
     * @param version Bytecode version.
     * @param access Access modifiers.
     * @param signature Class Signature.
     * @param supername Class supername.
     * @param interfaces Class interfaces.
     * @checkstyle ParameterNumberCheck (6 lines)
     */
    public DirectivesClassProperties(
        final Format format,
        final int version,
        final int access,
        final String signature,
        final String supername,
        final String... interfaces
    ) {
        this.format = format;
        this.version = version;
        this.access = access;
        this.signature = signature;
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
        if (this.supername != null) {
            directives.append(new DirectivesValue(this.format, "supername", this.supername));
        }
        if (this.interfaces != null) {
            directives.append(new DirectivesValues(this.format, "interfaces", this.interfaces));
        }
        directives.append(new DirectivesValue(this.format, "signature", this.sign()));
        return directives.iterator();
    }

    /**
     * The "signature" refers to generic type information in Java bytecode.
     * Contains information about:
     * Type parameters (generics) for classes and methods
     * Type bounds for generic parameters
     * Generic superclasses and interfaces
     * Generic field and method types
     * @return Signature of the class.
     * @todo #1183:90min Signature is a class attribute, not a property.
     *  Since 'signature' is a class attribute, but not a property, it makes sense to move
     *  it to a {@link DirectivesClass}, to attributes section.
     */
    private String sign() {
        return Optional.ofNullable(this.signature).orElse("");
    }
}
