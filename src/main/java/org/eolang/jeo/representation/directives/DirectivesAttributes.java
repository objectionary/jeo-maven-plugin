package org.eolang.jeo.representation.directives;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.xembly.Directive;
import org.xembly.Directives;

public final class DirectivesAttributes implements Iterable<Directive> {

    private final List<DirectivesAttribute> attributes;

    public DirectivesAttributes() {
        this(new ArrayList<>(0));
    }

    public DirectivesAttributes(final List<DirectivesAttribute> attributes) {
        this.attributes = attributes;
    }

    public DirectivesAttributes add(final DirectivesAttribute attribute) {
        this.attributes.add(attribute);
        return this;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Iterator<Directive> result;
        if (this.attributes.isEmpty()) {
            result = Collections.emptyIterator();
        } else {
            final Directives directives = new Directives().add("o")
                .attr("base", "tuple")
                .attr("star", "")
                .attr("name", "attributes");
            this.attributes.forEach(directives::append);
            result = directives.up().iterator();
        }
        return result;
    }
}
