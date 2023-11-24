package org.eolang.jeo.representation.directives;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eolang.jeo.representation.ClassName;
import org.xembly.Directive;
import org.xembly.Directives;

public class DirectivesClass implements Iterable<Directive> {

    private final ClassName name;
    private final DirectivesClassProperties properties;

    private final List<DirectivesMethod> methods;

    private final List<DirectivesField> fields;

    public DirectivesClass(final ClassName name) {
        this(name, new DirectivesClassProperties());
    }

    public DirectivesClass(final ClassName name, final DirectivesClassProperties properties) {
        this(name, properties, new ArrayList<>(0), new ArrayList<>(0));
    }

    public DirectivesClass(
        final ClassName name,
        final DirectivesClassProperties properties,
        final List<DirectivesMethodVisitor> methods,
        final List<DirectivesField> fields
    ) {
        this.name = name;
        this.properties = properties;
        this.methods = methods;
        this.fields = fields;
    }

    public DirectivesClass field(final DirectivesField field) {
        this.fields.add(field);
        return this;
    }

    public DirectivesClass method(final DirectivesMethod method) {
        this.methods.add(method);
        return this;
    }

    @Override
    public Iterator<Directive> iterator() {
        Directives directives = new Directives();
        directives.add("o")
            .attr("abstract", "")
            .attr("name", this.name.name())
            .append(this.properties);
        this.fields.forEach(directives::append);
        this.methods.forEach(directives::append);
        directives.up();
        return directives.iterator();
    }
}
