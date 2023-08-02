package org.eolang.jeo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Optimization {

    private final Path classes;
    private final Collection<Boost> boosts;

    public Optimization(final Path classes, Boost... boosts) {
        this(classes, Arrays.asList(boosts));
    }

    public Optimization(final Path classes, final Collection<Boost> boosts) {
        this.classes = classes;
        this.boosts = boosts;
    }

    void apply() throws IOException {
        this.bytecode()
            .stream()
            .map(BytecodeIR::new);
    }

    private void apply(final IR ir) {
        this.boosts.forEach(boost -> boost.apply(ir));
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
