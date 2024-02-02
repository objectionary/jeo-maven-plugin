package org.eolang.jeo.representation;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eolang.jeo.Details;
import org.eolang.jeo.Representation;
import org.eolang.jeo.representation.bytecode.Bytecode;

public final class LoggedRepresentation implements Representation {

    private static final Path UNKNOWN = Paths.get("Unknown");
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
        final Path source = this.details().source().orElse(LoggedRepresentation.UNKNOWN);
        if (Files.exists(source)) {
            try {
                Logger.info(
                    this,
                    "Disassembling '%[file]s' (%[size]s)",
                    source,
                    Files.size(source)
                );
                final long start = System.currentTimeMillis();
                final XML res = this.original.toEO();
                final long time = System.currentTimeMillis() - start;
                Logger.info(
                    this,
                    "'%[file]s' disassembled to '%[file]s' (%[size]s) in %[ms]s",
                    source,
                    this.details().destination().orElse(LoggedRepresentation.UNKNOWN),
                    (long) res.toString().getBytes(StandardCharsets.UTF_8).length,
                    time
                );
                return res;
            } catch (final IOException exception) {
                throw new RuntimeException(exception);
            }
        } else {
            return this.original.toEO();
        }
    }


    @Override
    public Bytecode toBytecode() {
//        final Path source = this.details().source().orElse(LoggedRepresentation.UNKNOWN);
//        try {
//            if (Files.exists(source)) {
//                Logger.info(
//                    this,
//                    "Assembling '%[file]s' (%[size]s)",
//                    source,
//                    Files.size(source)
//                );
//                final long start = System.currentTimeMillis();
//                final Bytecode res = this.original.toBytecode();
//                final long time = System.currentTimeMillis() - start;
//                Logger.info(
//                    this,
//                    "'%[file]s' assembled to '%[file]s' (%[size]s) in %[ms]s",
//                    source,
//                    this.details().destination().orElse(LoggedRepresentation.UNKNOWN),
//                    (long) res.asBytes().length,
//                    time
//                );
//                return res;
//            } else {
        return this.original.toBytecode();
//            }
//        } catch (final IOException exception) {
//            throw new RuntimeException(exception);
//        }
    }
}
