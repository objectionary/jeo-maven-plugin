package org.eolang.jeo.representation.directives;

import java.util.Collections;
import java.util.Iterator;
import org.eolang.jeo.representation.HexData;
import org.xembly.Directive;

public final class DirectivesNullable implements Iterable<Directive> {

    private final HexData data;
    private final String name;

    public <T> DirectivesNullable(final String name, final T data) {
        this(name, new HexData(data));
    }

    public DirectivesNullable(final String name, final HexData data) {
        this.name = name;
        this.data = data;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Iterator<Directive> result;
        if (this.data.isNull()) {
            result = Collections.emptyIterator();
        } else {
            result = new DirectivesData(this.data, this.name).iterator();
        }
        return result;
    }
}
