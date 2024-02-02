package org.eolang.jeo;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class TranslationLog implements Translation {
    private static final Path UNKNOWN = Paths.get("Unknown");

    private final String process;
    private final String participal;

    private final Translation original;

    public TranslationLog(
        final String process,
        final String participal,
        final Translation original
    ) {
        this.process = process;
        this.participal = participal;
        this.original = original;
    }

    @Override
    public Representation apply(
        final Representation repr
    ) {
        final Path source = repr.details().source().orElse(TranslationLog.UNKNOWN);
        try {
            if (Files.exists(source)) {
                Logger.info(
                    this,
                    "%s '%[file]s' (%[size]s)",
                    this.process,
                    source,
                    Files.size(source)
                );
                final long start = System.currentTimeMillis();
                final Representation apply = this.original.apply(repr);
                final long time = System.currentTimeMillis() - start;
                final Path after = apply.details().source().orElse(TranslationLog.UNKNOWN);
                Logger.info(
                    this,
                    "'%[file]s' %s to '%[file]s' (%[size]s) in %[ms]s",
                    source,
                    this.participal,
                    after,
                    Files.size(after),
                    time
                );
                return apply;
            } else {
                return this.original.apply(repr);
            }
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
