package org.eolang.jeo.representation.directives;

import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import org.xembly.Directive;
import org.xembly.Directives;

public final class DirectivesAnnotationDefault implements Iterable<Directive>, Composite {

    private final AtomicReference<Iterable<Directive>> value;

    public DirectivesAnnotationDefault() {
        this(new AtomicReference<>());
    }

    public DirectivesAnnotationDefault(final AtomicReference<Iterable<Directive>> value) {
        this.value = value;
    }

    @Override
    public Iterator<Directive> iterator() {
        if (this.value.get() != null) {
            final Directives directives = new Directives()
                .add("o")
                .attr("name", "annotation-default-value");
            directives.append(this.value.get());
            return directives.up().iterator();
        } else {
            return Collections.emptyIterator();
        }
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
