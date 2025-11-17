/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.PrefixedName;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives for generating EO class objects.
 *
 * <p>This class generates Xembly directives to create EO object representations
 * of Java classes, including their properties, fields, methods, annotations,
 * and attributes.</p>
 * <p>All the class directives are sorted according to JVM specification
 * {@code
 * ClassFile {
 *     u4             magic; (absent)
 *     u2             minor_version; {@link DirectivesClassProperties}
 *     u2             major_version; {@link DirectivesClassProperties}
 *     u2             constant_pool_count; (incorporated to the directives)
 *     cp_info        constant_pool[constant_pool_count-1]; (incorporated to the directives)
 *     u2             access_flags; {@link DirectivesClassProperties}
 *     u2             this_class; {@link DirectivesClass} (class name)
 *     u2             super_class;  {@link DirectivesClassProperties}
 *     u2             interfaces_count;  {@link DirectivesClassProperties}
 *     u2             interfaces[interfaces_count];  {@link DirectivesClassProperties}
 *     u2             fields_count; {@link DirectivesClass}
 *     field_info     fields[fields_count]; {@link DirectivesClass}
 *     u2             methods_count; {@link DirectivesClass}
 *     method_info    methods[methods_count]; {@link DirectivesClass}
 *     u2             attributes_count; {@link DirectivesClass}
 *     attribute_info attributes[attributes_count]; {@link DirectivesClass}
 * }}
 * <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.1">
 *     You can read more in the official JVM specification.
 * </a>
 * </p>
 * @since 0.1.0
 * @todo #1183:60min Class name should should be moved to DirectivesClassProperties
 *  Otherwise it breaks component ordering (see the JVM specification).
 *  The class name should be between access flags and superclasses - the both of them
 *  are defined the the {@link DirectivesClassProperties}.
 */
public final class DirectivesClass implements Iterable<Directive> {

    /**
     * Format of the directives.
     */
    private final Format format;

    /**
     * Class name.
     */
    private final ClassName name;

    /**
     * Class properties.
     */
    private final DirectivesClassProperties properties;

    /**
     * Class fields.
     */
    private final List<DirectivesField> fields;

    /**
     * Class methods.
     */
    private final List<DirectivesMethod> methods;

    /**
     * Annotations.
     */
    private final DirectivesAnnotations annotations;

    /**
     * Attributes.
     */
    private final DirectivesAttributes attributes;

    /**
     * Constructor.
     * @param name The class name
     */
    public DirectivesClass(final ClassName name) {
        this(name, new DirectivesClassProperties());
    }

    /**
     * Constructor.
     * @param classname The class name
     * @param properties The class properties
     */
    public DirectivesClass(final String classname, final DirectivesClassProperties properties) {
        this(new ClassName(classname), properties);
    }

    /**
     * Constructor.
     * @param name The class name
     * @param method The method to include
     */
    public DirectivesClass(final ClassName name, final DirectivesMethod method) {
        this(
            name,
            new DirectivesClassProperties(),
            new ArrayList<>(0),
            Collections.singletonList(method)
        );
    }

    /**
     * Constructor.
     * @param format The format of the directives
     * @param name The class name
     * @param properties The class properties
     * @param fields The class fields
     * @param methods The class methods
     * @param annotations The annotations
     * @param attributes The attributes
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesClass(
        final Format format,
        final ClassName name,
        final DirectivesClassProperties properties,
        final List<DirectivesField> fields,
        final List<DirectivesMethod> methods,
        final DirectivesAnnotations annotations,
        final DirectivesAttributes attributes
    ) {
        this.format = format;
        this.name = name;
        this.properties = properties;
        this.fields = fields;
        this.methods = methods;
        this.annotations = annotations;
        this.attributes = attributes;
    }

    /**
     * Constructor.
     * @param name The class name
     * @param field The field to include
     */
    DirectivesClass(final ClassName name, final DirectivesField field) {
        this(
            name,
            new DirectivesClassProperties(),
            Collections.singletonList(field),
            new ArrayList<>(0)
        );
    }

    /**
     * Constructor.
     * @param name The class name
     * @param properties The class properties
     */
    DirectivesClass(final ClassName name, final DirectivesClassProperties properties) {
        this(name, properties, new ArrayList<>(0), new ArrayList<>(0));
    }

    /**
     * Constructor.
     * @param name The class name
     * @param properties The class properties
     * @param fields The class fields
     * @param methods The class methods
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    private DirectivesClass(
        final ClassName name,
        final DirectivesClassProperties properties,
        final List<DirectivesField> fields,
        final List<DirectivesMethod> methods
    ) {
        this(
            new Format(),
            name,
            properties,
            fields,
            methods,
            new DirectivesAnnotations(),
            new DirectivesAttributes()
        );
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesGlobalObject(
            "class",
            new PrefixedName(this.name.name()).encode(),
            this.properties,
            new DirectivesValue(this.format, "name", this.name.full().replace('.', '/')),
            this.fields.stream().map(Directives::new).reduce(new Directives(), Directives::append),
            this.methods.stream().map(Directives::new).reduce(new Directives(), Directives::append),
            this.annotations,
            this.attributes
        ).iterator();
    }
}
