package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.eolang.jeo.representation.xmir.AllLabels;
import org.objectweb.asm.Label;
import org.xembly.Directive;
import org.xembly.Directives;

public class DirectivesLabel implements Iterable<Directive> {

    private final Label label;
    private final AllLabels all;

    public DirectivesLabel(final Label label) {
        this(label, new AllLabels());
    }

    public DirectivesLabel(final Label label, final AllLabels all) {
        this.label = label;
        this.all = all;
    }

    @Override
    public Iterator<Directive> iterator() {
        final String uid = this.all.uid(this.label);
        return new Directives().add("o")
            .attr("base", "label")
            .append(new DirectivesData(uid))
            .up()
            .iterator();
    }
}
