package org.eolang.jeo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import org.eolang.jeo.representation.XmirObject;
import org.eolang.jeo.representation.XmirRepresentation;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link EoObjects}.
 *
 * @since 0.1.0
 */
class EoObjectsTest {

    @Test
    void retrievesObjectsSuccessfully(@TempDir final Path temp) throws IOException {
        final int expected = 2;
        final Path directory = temp.resolve(new XmirDefaultDirectory().toPath());
        Files.createDirectories(directory);
        Files.write(directory.resolve("first.xmir"), new XmirObject("some object")
            .xml()
            .toString()
            .getBytes(StandardCharsets.UTF_8)
        );
        Files.write(directory.resolve("second.xmir"), new XmirObject("some object")
            .xml()
            .toString()
            .getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            String.format("Objects were not retrieved, we expected '%d' objects", expected),
            new EoObjects(temp).objects(),
            Matchers.hasSize(expected)
        );
    }


}