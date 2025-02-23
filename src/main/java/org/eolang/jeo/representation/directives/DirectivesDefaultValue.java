/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;

/**
 * This class represents a default annotation value.
 * <p>
 *     {@code
 *        public @interface NestedAnnotation {
 *          String name() default "nested-default";
 *        }
 *     }
 * </p>
 * For example, in the code above, the default value is "nested-default".
 *
 * @since 0.3
 */
public final class DirectivesDefaultValue implements Iterable<Directive> {

    /**
     * Default value.
     */
    private final Iterable<Directive> value;

    /**
     * Constructor.
     * @param value Default value.
     */
    public DirectivesDefaultValue(final Iterable<Directive> value) {
        this.value = value;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "annotation-default-value", "annotation-defvalue", this.value
        ).iterator();
    }
}
