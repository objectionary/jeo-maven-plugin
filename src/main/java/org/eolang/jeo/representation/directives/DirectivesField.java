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

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Field directives.
 * Any java field will be transformed into the following EO object.
 *  <p>
 *     {@code
 *       [access descriptor signature value] > field
 *     }
 * </p>
 * The name of the "field" object is a name of the field in Java class.
 * For example, the following Java field
 * <p>
 *     {@code
 *        private final int bar = 1;
 *     }
 * </p>
 * will be transformed into the following EO object:
 * <p>
 *     {@code
 *       field 18 "I" "" "01" > bar
 *     }
 * </p>
 *
 * @since 0.1
 */
public final class DirectivesField implements Iterable<Directive> {

    /**
     * Access.
     */
    private final int access;

    /**
     * Name.
     */
    private final String name;

    /**
     * Descriptor.
     */
    private final String descriptor;

    /**
     * Signature.
     */
    private final String signature;

    /**
     * Initial value.
     */
    private final Object value;

    /**
     * Annotations.
     */
    private final DirectivesAnnotations annotations;

    /**
     * Constructor.
     */
    public DirectivesField() {
        this(Opcodes.ACC_PUBLIC, "unknown", "I", "", "0");
    }

    /**
     * Constructor.
     * @param access Access
     * @param name Name
     * @param descriptor Descriptor
     * @param signature Signature
     * @param value Initial value
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesField(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final Object value
    ) {
        this.access = access;
        this.name = name;
        this.descriptor = Optional.ofNullable(descriptor).orElse("");
        this.signature = Optional.ofNullable(signature).orElse("");
        this.value = Optional.ofNullable(value).orElse("");
        this.annotations = new DirectivesAnnotations();
    }

    /**
     * Add annotation.
     * @param annotation Annotation
     * @return This object
     */
    public DirectivesField annotation(final DirectivesAnnotation annotation) {
        this.annotations.add(annotation);
        return this;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new Directives().add("o")
            .attr("base", "field")
            .attr("name", this.name)
            .append(new DirectivesData(this.name("access"), this.access))
            .append(new DirectivesData(this.name("descriptor"), this.descriptor))
            .append(new DirectivesData(this.name("signature"), this.signature))
            .append(new DirectivesData(this.name("value"), this.value))
            .append(this.annotations)
            .up()
            .iterator();
    }

    private String name(final String prefix) {
        final String template = "%s-%s";
        return String.format(template, prefix, this.name);
    }

}
