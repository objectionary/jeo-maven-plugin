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
package org.eolang.jeo.representation.directives;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eolang.jeo.representation.ClassName;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives Class.
 * @since 0.1
 */
public final class DirectivesClass implements Iterable<Directive> {

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
     * @param name Class name
     */
    public DirectivesClass(final ClassName name) {
        this(name, new DirectivesClassProperties());
    }

    /**
     * Constructor.
     * @param classname Class name.
     * @param properties Class properties.
     */
    public DirectivesClass(final String classname, final DirectivesClassProperties properties) {
        this(new ClassName(classname), properties);
    }

    /**
     * Constructor.
     * @param name Class name
     * @param method Method
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
     * @param name Class name
     * @param properties Class properties
     * @param fields Class fields
     * @param methods Class methods
     * @param annotations Annotations
     * @param attributes Attributes
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesClass(
        final ClassName name,
        final DirectivesClassProperties properties,
        final List<DirectivesField> fields,
        final List<DirectivesMethod> methods,
        final DirectivesAnnotations annotations,
        final DirectivesAttributes attributes
    ) {
        this.name = name;
        this.properties = properties;
        this.fields = fields;
        this.methods = methods;
        this.annotations = annotations;
        this.attributes = attributes;
    }

    /**
     * Constructor.
     * @param name Class name
     * @param field Field
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
     * @param name Class name
     * @param properties Class properties
     */
    DirectivesClass(final ClassName name, final DirectivesClassProperties properties) {
        this(name, properties, new ArrayList<>(0), new ArrayList<>(0));
    }

    /**
     * Constructor.
     * @param name Class name
     * @param properties Class properties
     * @param fields Class fields
     * @param methods Class methods
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    private DirectivesClass(
        final ClassName name,
        final DirectivesClassProperties properties,
        final List<DirectivesField> fields,
        final List<DirectivesMethod> methods
    ) {
        this(
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
        return new DirectivesAbstractObject(
            this.name.name(),
            this.properties,
            this.fields.stream().map(Directives::new).reduce(new Directives(), Directives::append),
            this.methods.stream().map(Directives::new).reduce(new Directives(), Directives::append),
            this.annotations,
            this.attributes
        ).iterator();
    }
}
