package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;
import org.xembly.Directives;

public class DirectivesAnnotation implements Iterable<Directive> {

    private final String descriptor;

    private final boolean visible;

    public DirectivesAnnotation(
        final String descriptor,
        final boolean visible
    ) {
        this.descriptor = descriptor;
        this.visible = visible;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new Directives().add("o")
            .append(new DirectivesData("descriptor", this.descriptor))
            .append(new DirectivesData("visible", this.visible))
            .iterator();
    }
}
