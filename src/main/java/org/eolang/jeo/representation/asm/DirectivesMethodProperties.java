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

    private final String[] interfaces;

    public DirectivesMethodProperties(
        final int access,
        final String desciptor,
        final String signature,
        final String... interfaces
    ) {
        this.access = access;
        this.desciptor = Optional.ofNullable(desciptor).orElse("");
        this.signature = Optional.ofNullable(signature).orElse("");
        this.interfaces = Optional.ofNullable(interfaces).orElse(new String[0]).clone();
    }

    @Override
    public Iterator<Directive> iterator() {
        return new Directives()
            .append(new DirectivesData(this.access, "access").directives())
            .append(new DirectivesData(this.desciptor, "descriptor").directives())
            .append(new DirectivesData(this.signature, "signature").directives())
//            .append(new DirectivesData(this.interfaces, "interfaces").directives())
            .append(this.arguments())
            .iterator();
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
