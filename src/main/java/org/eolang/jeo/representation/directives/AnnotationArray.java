package org.eolang.jeo.representation.directives;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xembly.Directive;
import org.xembly.Directives;

public final class AnnotationArray implements Appendable, Iterable<Directive> {

    private final List<Iterable<Directive>> all;

    public AnnotationArray() {
        this(new ArrayList<>(0));
    }

    public AnnotationArray(final List<Iterable<Directive>> all) {
        this.all = all;
    }

    @Override
    public void append(final Iterable<Directive> directives) {
        this.all.add(directives);
    }

    @Override
    public Iterable<Directive> sum() {
        Directives directives = new Directives();
        directives.add("o").attr("base", "annotations-array");
        this.all.forEach(directives::append);
        return directives.up();
    }

    @Override
    public Iterator<Directive> iterator() {
        return this.sum().iterator();
    }
}
