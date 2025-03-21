/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.PrefixedName;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;

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
@ToString
@EqualsAndHashCode
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
        this(Opcodes.ACC_PUBLIC, "unknown", "I", "", 0);
    }

    /**
     * Constructor.
     * @param annotations Annotations.
     */
    public DirectivesField(final DirectivesAnnotation... annotations) {
        this(
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
        this(
            access,
            name,
            descriptor,
            signature,
            value,
            new DirectivesAnnotations(String.format("annotations-%s", name))
        );
    }

    /**
     * Constructor.
     * @param access Access modifiers
     * @param name Name
     * @param descriptor Descriptor
     * @param signature Signature
     * @param value Initial value
     * @param annotations Annotations
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesField(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final Object value,
        final DirectivesAnnotations annotations
    ) {
        this.access = access;
        this.name = name;
        this.descriptor = Optional.ofNullable(descriptor).orElse("");
        this.signature = Optional.ofNullable(signature).orElse("");
        this.value = value;
        this.annotations = annotations;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "field",
            new PrefixedName(this.name).encode(),
            new DirectivesValue(this.title("access"), this.access),
            new DirectivesValue(this.title("descriptor"), this.descriptor),
            new DirectivesValue(this.title("signature"), this.signature),
            new DirectivesValue(this.title("value"), this.value),
            this.annotations
        ).iterator();
    }

    /**
     * Field property title.
     * It is used to create a title for the field property to make XMIR more readable.
     * @param prefix Prefix
     * @return Title
     */
    private String title(final String prefix) {
        final String template = "%s-%s";
        return String.format(template, prefix, this.name);
    }
}
