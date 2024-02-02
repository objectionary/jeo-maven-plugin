package org.eolang.jeo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eolang.jeo.representation.JavaName;
import org.eolang.jeo.representation.XmirRepresentation;

public final class Disassemble implements Translation {

    /**
     * Where to save the EO.
     */
    private final Path target;

    public Disassemble(final Path target) {
        this.target = target;
    }

    @Override
    public Representation apply(final Representation original) {
        final String name = new JavaName(original.details().name()).decode();
        final Path path = this.target
            .resolve(String.format("%s.xmir", name.replace('/', File.separatorChar)));
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, original.toEO().toString().getBytes(StandardCharsets.UTF_8));
            return new XmirRepresentation(path);
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't save XML to %s", path),
                exception
            );
        }
    }
}
