package org.eolang.jeo;

import com.jcabi.xml.XMLDocument;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.XmirRepresentation;

final class EoObjects {

    private final Path objectspath;

    EoObjects(final Path objects) {
        this.objectspath = objects;
    }

    Collection<XmirRepresentation> objects() {
        final Path path = this.objectspath.resolve("jeo")
            .resolve("xmir");
        try (Stream<Path> walk = Files.walk(path)) {
            return walk.filter(Files::isRegularFile)
                .map(EoObjects::toXML)
                .map(XmirRepresentation::new)
                .collect(Collectors.toList());
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't read folder '%s'", path),
                exception
            );
        }
    }

    /**
     * Convert path to XML.
     * @param path Path to XML file.
     * @return XML.
     */
    private static XMLDocument toXML(final Path path) {
        try {
            return new XMLDocument(path.toFile());
        } catch (final FileNotFoundException exception) {
            throw new IllegalStateException(
                String.format("Can't find file '%s'", path),
                exception
            );
        }
    }

}
