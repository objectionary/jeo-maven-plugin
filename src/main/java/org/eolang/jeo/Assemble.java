package org.eolang.jeo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.JavaName;

public final class Assemble implements Translation {

    private final Path classes;

    public Assemble(final Path classes) {
        this.classes = classes;
    }

    @Override
    public Representation apply(final Representation original) {
        final Details details = original.details();
        final String name = new JavaName(details.name()).decode();
        try {
            final byte[] bytecode = original.toBytecode().asBytes();
            final String[] subpath = name.split("\\.");
            subpath[subpath.length - 1] = String.format("%s.class", subpath[subpath.length - 1]);
            final Path path = Paths.get(this.classes.toString(), subpath);
            Files.createDirectories(path.getParent());
            Files.write(path, bytecode);
            return new BytecodeRepresentation(path);
        } catch (final IOException exception) {
            throw new IllegalStateException(String.format("Can't recompile '%s'", name), exception);
        }
    }
}
