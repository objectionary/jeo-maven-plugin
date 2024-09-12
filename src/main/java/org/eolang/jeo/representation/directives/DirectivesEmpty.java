package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;
import org.xembly.Directives;

public final class DirectivesEmpty implements Iterable<Directive> {

    @Override
    public Iterator<Directive> iterator() {
        return new Directives().iterator();
    }
}
