package org.eolang.jeo;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;

public class XmirFootprint implements Boost {

    private final Path home;

    public XmirFootprint(final Path home) {
        this.home = home;
    }

    @Override
    public Collection<IR> apply(final Collection<IR> representations) {
        representations.stream().map(IR::toEO).forEach(this::tryToSave);
        return representations;
    }

    private void tryToSave(final XML xml) {
        try {
            Files.write(
                this.home.resolve(String.format("%s.xml", UUID.randomUUID())),
                xml.toString().getBytes(StandardCharsets.UTF_8)
            );
        } catch (IOException ex) {
            throw new IllegalStateException(
                String.format("Can't save XML to %s", this.home),
                ex
            );
        }
    }
}
