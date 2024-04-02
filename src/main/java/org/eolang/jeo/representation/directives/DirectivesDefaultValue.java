package org.eolang.jeo.representation.directives;

import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import org.xembly.Directive;
import org.xembly.Directives;

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
public final class DirectivesDefaultValue implements Iterable<Directive>, Composite {

    /**
     * Default value.
     */
    private final AtomicReference<Iterable<Directive>> value;

    /**
     * Constructor.
     */
    public DirectivesDefaultValue() {
        this(new AtomicReference<>());
    }

    /**
     * Constructor.
     * @param value Default value.
     */
    public DirectivesDefaultValue(final AtomicReference<Iterable<Directive>> value) {
        this.value = value;
    }

    @Override
    public Iterator<Directive> iterator() {
        if (this.value.get() != null) {
            final String label = "annotation-default-value";
            final Directives directives = new Directives()
                .add("o")
                .attr("base", label)
                .attr("name", label);
            directives.append(this.value.get());
            return directives.up().iterator();
        } else {
            return new Directives().iterator();
        }
    }

    /**
     * Check if the default value is empty.
     * @return True if the default value is empty, otherwise false.
     */
    public boolean isEmpty() {
        return this.value.get() == null;
    }

    @Override
    public void append(final Iterable<Directive> directives) {
        this.value.set(directives);
    }

    @Override
    public Iterable<Directive> build() {
        return this;
    }
}
