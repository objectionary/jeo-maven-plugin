package org.eolang.jeo.representation.directives;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.xembly.Directive;
import org.xembly.Directives;

public final class DirectivesAttribute implements Iterable<Directive> {

    private final String name;
    private final List<Iterable<Directive>> data;

    @SafeVarargs
    public DirectivesAttribute(final String name, final Iterable<Directive>... data) {
        this(name, Arrays.asList(data));
    }

    public DirectivesAttribute(final String name, final List<Iterable<Directive>> data) {
        this.name = name;
        this.data = data;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives directives = new Directives().add("o").attr("name", this.name);
        this.data.forEach(directives::append);
        return directives.up().iterator();
    }
}
