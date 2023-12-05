package org.eolang.jeo.representation.directives;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import org.eolang.jeo.representation.ClassName;
import org.xembly.Directive;
import org.xembly.Directives;

public class DirectivesProgram implements Iterable<Directive> {

    final String listing;
    final AtomicReference<DirectivesClass> klass;
    final AtomicReference<ClassName> classname;

    public DirectivesProgram(final String listing) {
        this(listing, new AtomicReference<>(), new AtomicReference<>());
    }

    private DirectivesProgram(
        final String listing,
        final AtomicReference<DirectivesClass> klass,
        final AtomicReference<ClassName> classname
    ) {
        this.listing = listing;
        this.klass = klass;
        this.classname = classname;
    }

    DirectivesProgram withClass(ClassName name, DirectivesClass klass) {
        this.classname.set(name);
        this.klass.set(klass);
        return this;
    }

    DirectivesClass top() {
        if (Objects.isNull(this.klass.get())) {
            throw new IllegalStateException(
                String.format("Class is not initialized here %s", this)
            );
        }
        return this.klass.get();
    }

    @Override
    public Iterator<Directive> iterator() {
        final String now = ZonedDateTime.now(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT);
        Directives directives = new Directives();
        directives.add("program")
            .attr("name", this.classname.get().name())
            .attr("version", "0.0.0")
            .attr("revision", "0.0.0")
            .attr("dob", now)
            .attr("time", now)
            .add("listing")
            .set(this.listing)
            .up()
            .add("errors").up()
            .add("sheets").up()
            .add("license").up()
            .append(new DirectivesMetas(this.classname.get()))
            .attr("ms", System.currentTimeMillis())
            .add("objects");
        directives.append(this.klass.get());
        directives.up();
        return directives.iterator();
    }
}
