/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.Counter;
import org.eolang.jeo.representation.directives.Fingerprint;
import org.eolang.jeo.representation.directives.Format;

/**
 * Disassembler for bytecode classes.
 *
 * <p>This class disassembles the project's compiled Java bytecode classes into XMIR (EO XML
 * representation). It processes all .class files from a specified directory and converts them
 * into corresponding XMIR files, supporting different disassembly modes for various levels of
 * detail.</p>
 *
 * @since 0.1.0
 */
public final class Disassembler {

    /**
     * Project compiled classes.
     */
    private final Classes classes;

    /**
     * Where to save decompiled classes.
     */
    private final Path target;

    /**
     * Disassemble params.
     */
    private final Format params;

    /**
     * Enables detailed debug logging.
     */
    private final boolean debug;

    /**
     * Number of threads for parallel processing.
     *
     * <p>When 0, the number of available processors is used automatically.</p>
     */
    private final int threads;

    /**
     * Constructor.
     *
     * @param classes Directory containing compiled class files
     * @param target Target directory where XMIR files will be saved
     */
    public Disassembler(final Path classes, final Path target) {
        this(classes, target, new Format());
    }

    /**
     * Constructor.
     *
     * @param classes Directory containing compiled class files
     * @param target Target directory where XMIR files will be saved
     * @param params Disassembling params
     */
    public Disassembler(
        final Path classes,
        final Path target,
        final Format params
    ) {
        this(new BytecodeClasses(classes), target, params, false);
    }

    /**
     * Constructor.
     *
     * @param classes Project compiled classes
     * @param target Where to save decompiled classes
     * @param params Disassembling params
     * @param debug Enables detailed debug logging
     */
    public Disassembler(
        final Classes classes,
        final Path target,
        final Format params,
        final boolean debug
    ) {
        this(classes, target, params, debug, 0);
    }

    /**
     * Constructor.
     *
     * @param classes Project compiled classes
     * @param target Where to save decompiled classes
     * @param params Disassembling params
     * @param debug Enables detailed debug logging
     * @param threads Number of threads (0 = use available processors automatically)
     */
    public Disassembler(
        final Classes classes,
        final Path target,
        final Format params,
        final boolean debug,
        final int threads
    ) {
        this.classes = classes;
        this.target = target;
        this.params = params;
        this.debug = debug;
        this.threads = threads;
    }

    /**
     * Disassemble all bytecode files.
     */
    public void disassemble() {
        final List<Path> paths = this.classes.all().collect(Collectors.toList());
        final Counter counter = new Counter(paths.size());
        try (
            Stream<Path> stream = new Summary(
                "Disassembling",
                "disassembled",
                this.classes.toString(),
                this.target,
                new ParallelTranslator(path -> this.disassemble(path, counter), this.threads)
            ).apply(paths.stream())
        ) {
            final Set<Path> produced = stream.peek(this::log)
                .map(path -> path.toAbsolutePath().normalize())
                .collect(Collectors.toSet());
            this.clean(produced);
        }
    }

    private void clean(final Set<Path> produced) {
        if (Files.isDirectory(this.target)) {
            try (Stream<Path> all = Files.walk(this.target)) {
                final List<Path> stale = all
                    .filter(path -> path.getFileName().toString().endsWith(".xmir"))
                    .filter(path -> !produced.contains(path.toAbsolutePath().normalize()))
                    .collect(Collectors.toList());
                for (final Path path : stale) {
                    Files.delete(path);
                    Files.deleteIfExists(
                        path.resolveSibling(String.format("%s.jeo-cache", path.getFileName()))
                    );
                    Logger.info(this, "Removed %[file]s, since it has no class anymore", path);
                }
            } catch (final IOException exception) {
                throw new IllegalStateException(
                    String.format("Failed to remove stale XMIR files from '%s'", this.target),
                    exception
                );
            }
        }
    }

    private Path disassemble(final Path path, final Counter counter) {
        final Transformation trans = new Caching(
            new Logging(
                "Disassembling",
                "disassembled",
                new Informative(
                    new Disassembling(this.classes.root(), this.target, path, this.params)
                ),
                this.debug,
                counter
            ),
            new Fingerprint(this.params).toString()
        );
        trans.transform();
        return trans.target();
    }

    private void log(final Path disassembled) {
        try {
            Logger.debug(
                this,
                "Disassembling of %[file]s (%[size]s) finished successfully",
                disassembled,
                Files.size(disassembled)
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to get size of '%s'",
                    disassembled
                ),
                exception
            );
        }
    }
}
