package org.eolang.jeo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Optimization {

    private final Path classes;
    private final Boost boosts;

    Optimization(final Path classes, final Boost boosts) {
        this.classes = classes;
        this.boosts = boosts;
    }

    void apply() throws IOException {
        this.boosts.apply(this.bytecode()
                .stream()
                .map(BytecodeIR::new)
                .collect(Collectors.toList()));
    }

    /**
     * Find all bytecode files.
     * @return Collection of bytecode files
     * @throws IOException If some I/O problem arises
     */
    private Collection<Path> bytecode() throws IOException {
        if (Objects.isNull(this.classes)) {
            throw new IllegalStateException(
                "The classes directory is not set, jeo-maven-plugin does not know where to look for classes."
            );
        }
        try (Stream<Path> walk = Files.walk(this.classes)) {
            return walk.filter(path -> path.toString().endsWith(".class"))
                .collect(Collectors.toList());
        }
    }
}
