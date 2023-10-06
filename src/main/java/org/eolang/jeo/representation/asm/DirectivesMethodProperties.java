package org.eolang.jeo.representation.asm;

import java.util.Iterator;
import java.util.Optional;
import org.xembly.Directive;
import org.xembly.Directives;
import org.objectweb.asm.Type;

public class DirectivesMethodProperties implements Iterable<Directive> {

    private final int access;
    private final String desciptor;

    private final String signature;

    private final String[] exceptions;

    DirectivesMethodProperties(
        final int access,
        final String desciptor,
        final String signature,
        final String... exceptions
    ) {
        this.access = access;
        this.desciptor = Optional.ofNullable(desciptor).orElse("");
        this.signature = Optional.ofNullable(signature).orElse("");
        this.exceptions = Optional.ofNullable(exceptions).orElse(new String[0]).clone();
    }

    @Override
    public Iterator<Directive> iterator() {
        return new Directives()
            .append(new DirectivesData(this.access, "access").directives())
            .append(new DirectivesData(this.desciptor, "descriptor").directives())
            .append(new DirectivesData(this.signature, "signature").directives())
            .append(this.exceptions())
            .append(this.arguments())
            .iterator();
    }

    private Directives exceptions() {
        final Directives tuple = new Directives()
            .add("o")
            .attr("base", "tuple")
            .attr("data", "tuple")
            .attr("name", "exceptions");
        for (final String exception : this.exceptions) {
            tuple.append(new DirectivesData(exception).directives());
        }
        tuple.up();
        return tuple;
    }

    private Directives arguments() {
        Directives directives = new Directives();
        final Type[] arguments = Type.getArgumentTypes(this.desciptor);
        for (int index = 0; index < arguments.length; ++index) {
            directives.add("o")
                .attr("abstract", "")
                .attr("name", String.format("arg__%s__%d", arguments[index], index))
                .up();
        }
        return directives;
    }

}
