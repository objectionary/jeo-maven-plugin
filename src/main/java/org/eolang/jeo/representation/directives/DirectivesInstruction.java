package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;
import org.xembly.Directives;

public class DirectivesInstruction implements Iterable<Directive> {

    private final int opcode;
    private final Object[] arguments;

    public DirectivesInstruction(final int opcode, final Object... arguments) {
        this.opcode = opcode;
        this.arguments = arguments;
    }

    @Override
    public Iterator<Directive> iterator() {
        Directives directives = new Directives();
        directives.add("o")
            .attr("name", new OpcodeName(this.opcode).asString())
            .attr("base", "opcode");
        for (final Object operand : this.arguments) {
            directives.append(new DirectivesOperand(operand));
        }
        directives.up();
        return directives.iterator();
    }
}
