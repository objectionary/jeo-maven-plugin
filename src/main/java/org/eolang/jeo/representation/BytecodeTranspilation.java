package org.eolang.jeo.representation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.improvement.XmirFootprint;

public class BytecodeTranspilation {

    /**
     * Project compiled classes.
     */
    private final Path classes;

    /**
     * Project default target directory.
     */
    private final Path target;


    public BytecodeTranspilation(
        final Path classes,
        final Path target
    ) {
        this.classes = classes;
        this.target = target;
    }

    public void transpile() throws IOException {
        new XmirFootprint(this.target)
            .apply(
                this.bytecode()
                    .stream()
                    .map(BytecodeRepresentation::new)
                    .collect(Collectors.toList())
            );
    }

    /**
     * Find all bytecode files.
     * @return Collection of bytecode files
     * @throws java.io.IOException If some I/O problem arises
     */
    private Collection<Path> bytecode() throws IOException {
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
