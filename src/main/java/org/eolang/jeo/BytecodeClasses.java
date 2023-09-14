package org.eolang.jeo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.BytecodeRepresentation;

public class BytecodeClasses {


    /**
     * Project compiled classes.
     */
    private final Path classes;

    public BytecodeClasses(final Path classes) {
        this.classes = classes;
    }

    public Collection<BytecodeRepresentation> bytecode() throws IOException {
        return this.classes()
            .stream()
            .map(BytecodeRepresentation::new)
            .collect(Collectors.toList());
    }

    /**
     * Find all bytecode files.
     * @return Collection of bytecode files
     * @throws java.io.IOException If some I/O problem arises
     */
    private Collection<Path> classes() throws IOException {
        if (Objects.isNull(this.classes)) {
            throw new IllegalStateException(
                "The classes directory is not set, jeo-maven-plugin does not know where to look for classes."
            );
        }
        if (!Files.exists(this.classes)) {
            throw new IllegalStateException(
                String.format(
                    "The classes directory '%s' does not exist, jeo-maven-plugin does not know where to look for classes.",
                    this.classes
                )
            );
        }
        try (Stream<Path> walk = Files.walk(this.classes)) {
            return walk.filter(path -> path.toString().endsWith(".class"))
                .collect(Collectors.toList());
        }
    }
}
