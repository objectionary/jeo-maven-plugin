package org.eolang.jeo.representation;

import com.jcabi.xml.XML;
import org.eolang.jeo.Details;
import org.eolang.jeo.Representation;
import org.eolang.jeo.representation.bytecode.Bytecode;

public final class LoggedRepresentation implements Representation {

    private final Representation original;

    public LoggedRepresentation(final Representation original) {
        this.original = original;
    }

    @Override
    public Details details() {
        return this.original.details();
    }

    @Override
    public XML toEO() {
        return this.original.toEO();
    }

    @Override
    public Bytecode toBytecode() {
        return this.original.toBytecode();
    }
}
