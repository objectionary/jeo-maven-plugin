package org.eolang.jeo;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class SingleTranslationLog implements SingleTranslation {
    private static final Path UNKNOWN = Paths.get("Unknown");

    private final SingleTranslation original;

    public SingleTranslationLog(final SingleTranslation original) {
        this.original = original;
    }

    @Override
    public Representation apply(
        final Representation repr
    ) {
        final Path source = repr.details().source().orElse(SingleTranslationLog.UNKNOWN);
        try {
            if (Files.exists(source)) {
                Logger.info(
                    this,
                    "Assembling '%[file]s' (%[size]s)",
                    source,
                    Files.size(source)
                );
                final long start = System.currentTimeMillis();
                final Representation apply = this.original.apply(repr);
                final long time = System.currentTimeMillis() - start;
                final Path after = apply.details().source().orElse(SingleTranslationLog.UNKNOWN);
                Logger.info(
                    this,
                    "'%[file]s' assembled to '%[file]s' (%[size]s) in %[ms]s",
                    source,
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
