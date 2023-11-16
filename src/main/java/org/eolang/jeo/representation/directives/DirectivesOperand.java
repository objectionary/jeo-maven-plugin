package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.objectweb.asm.Label;
import org.xembly.Directive;

/**
 * Operand XML directives.
 * @since 0.1
 */
final class DirectivesOperand implements Iterable<Directive> {

    /**
     * Raw operand.
     */
    private final Object raw;

    /**
     * Constructor.
     * @param operand Raw operand.
     */
    DirectivesOperand(final Object operand) {
        this.raw = operand;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Iterator<Directive> result;
        if (this.raw instanceof Label) {
            result = new DirectivesLabel((Label) this.raw).iterator();
        } else {
            result = new DirectivesData(this.raw).iterator();
        }
        return result;
    }
}
