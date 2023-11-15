package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

public class DirectivesMethodParams implements Iterable<Directive> {

    private final String descriptor;

    public DirectivesMethodParams(final String descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives directives = new Directives();
        final Type[] arguments = Type.getArgumentTypes(this.descriptor);
        for (int index = 0; index < arguments.length; ++index) {
            directives.add("o")
                .attr("abstract", "")
                .attr("name", String.format("arg__%s__%d", arguments[index], index))
                .up();
        }
        return directives.iterator();
    }
}
