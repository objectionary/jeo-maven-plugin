package org.eolang.jeo.representation.directives;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xembly.Directive;
import org.xembly.Directives;

public class DirectivesMethod implements Iterable<Directive> {

    private final String name;
    private final DirectivesMethodProperties properties;

    private final List<DirectivesInstruction> instructions;

    public DirectivesMethod(final String name, final DirectivesMethodProperties properties) {
        this.name = name;
        this.properties = properties;
        this.instructions = new ArrayList<>(0);
    }

    /**
     * Add opcode to the directives.
     * @param opcode Opcode
     * @param operands Operands
     */
    public void opcode(final int opcode, final Object... operands) {
        this.instructions.add(new DirectivesInstruction(opcode, operands));
    }

    @Override
    public Iterator<Directive> iterator() {
        Directives directives = new Directives();
        final String name;
        if (this.name.equals("<init>")) {
            name = "new";
        } else {
            name = this.name;
        }
        directives.add("o")
            .attr("abstract", "")
            .attr("name", name)
            .append(this.properties)
            .add("o")
            .attr("base", "seq")
            .attr("name", "@");
        this.instructions.forEach(directives::append);
        directives.up().up();
        return directives.iterator();
    }
}
