/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import java.util.Objects;
import org.eolang.jeo.representation.PrefixedName;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;

/**
 * Field directives.
 * Any java field will be transformed into the following EO object.
 *
 * <p>{@code [access descriptor signature value] > field }</p>
 * The name of the "field" object is a name of the field in Java class.
 * For example, the following Java field
 *
 * <p>{@code private final int bar = 1; }</p>
 * will be transformed into the following EO object:
 *
 * <p>{@code field 18 "I" "" "01" > bar }</p>
 *
 * <p>All the directives inside this class are sorted according to the JVM specification: {@code
 * field_info { u2 access_flags; u2 name_index; u2 descriptor_index; u2 attributes_count;
 * attribute_info attributes[attributes_count]; (signature, annotations, value) }}</p>
 *
 * @since 0.1
 */
public final class DirectivesField implements Iterable<Directive> {

    /**
     * Format of the directives.
     */
    private final Format format;

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
        this(Opcodes.ACC_PUBLIC, "unknown", "I", "", 0);
    }

    /**
     * Constructor.
     *
     * @param annotations Annotations
     */
    public DirectivesField(final DirectivesAnnotation... annotations) {
        this(
            new Format(),
            Opcodes.ACC_PUBLIC,
            "unknown",
            "I",
            "",
            0,
            new DirectivesAnnotations("annotations-unknown", annotations)
        );
    }

    /**
     * Constructor.
     *
     * @param access Access
     * @param name Name
     * @param descriptor Descriptor
     * @param signature Signature
     * @param value Initial value
     */
    public DirectivesField(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final Object value
    ) {
        this(
            new Format(),
            access,
            name,
            descriptor,
            signature,
            value,
            new DirectivesAnnotations("annotations")
        );
    }

    /**
     * Constructor.
     *
     * @param format Format
     * @param access Access modifiers
     * @param name Name
     * @param descriptor Descriptor
     * @param signature Signature
     * @param value Initial value
     * @param annotations Annotations
     */
    public DirectivesField(
        final Format format,
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final Object value,
        final DirectivesAnnotations annotations
    ) {
        this.format = format;
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.value = value;
        this.annotations = annotations;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "field",
            new PrefixedName(this.name).encode(),
            new DirectivesValue(this.format, DirectivesField.title("access"), this.access),
            new DirectivesValue(this.format, DirectivesField.title("name"), this.name),
            new DirectivesValue(
                this.format, DirectivesField.title("descriptor"), this.descriptor()
            ),
            new DirectivesValue(
                this.format, DirectivesField.title("signature"), this.signature()
            ),
            new DirectivesValue(this.format, DirectivesField.title("value"), this.value),
            this.annotations
        ).iterator();
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof DirectivesField) {
            final DirectivesField field = (DirectivesField) other;
            result = this.access == field.access
                && Objects.equals(this.name, field.name)
                && Objects.equals(this.descriptor(), field.descriptor())
                && Objects.equals(this.signature(), field.signature())
                && Objects.equals(this.value, field.value)
                && Objects.equals(this.annotations, field.annotations);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            this.access, this.name, this.descriptor(), this.signature(),
            this.value, this.annotations
        );
    }

    @Override
    public String toString() {
        return String.format(
            "DirectivesField(access=%d, name=%s, descriptor=%s, signature=%s, value=%s, annotations=%s)",
            this.access, this.name, this.descriptor(), this.signature(),
            this.value, this.annotations
        );
    }

    private static String title(final String prefix) {
        return prefix;
    }

    private String descriptor() {
        final String result;
        if (this.descriptor == null) {
            result = "";
        } else {
            result = this.descriptor;
        }
        return result;
    }

    private String signature() {
        final String result;
        if (this.signature == null) {
            result = "";
        } else {
            result = this.signature;
        }
        return result;
    }
}
