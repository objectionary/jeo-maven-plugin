package org.eolang.jeo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eolang.jeo.representation.Xmir;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link EoObjects}.
 *
 * @since 0.1.0
 */
final class EoObjectsTest {

    @Test
    void retrievesObjectsSuccessfully(@TempDir final Path temp) throws IOException {
        final int expected = 2;
        final Path directory = temp.resolve(new XmirDefaultDirectory().toPath());
        Files.createDirectories(directory);
        Files.write(directory.resolve("first.xmir"), new Xmir().bytes());
        Files.write(directory.resolve("second.xmir"), new Xmir().bytes());
        MatcherAssert.assertThat(
            String.format("Objects were not retrieved, we expected '%d' objects", expected),
            new EoObjects(temp).objects(),
            Matchers.hasSize(expected)
        );
    }

    @Test
    void retrievesEmptyObjectsIfFolderIsEmpty(@TempDir final Path temp) throws IOException {
        Files.createDirectories(temp.resolve(new XmirDefaultDirectory().toPath()));
        MatcherAssert.assertThat(
            "Objects were not retrieved, we expected empty list",
            new EoObjects(temp).objects(),
            Matchers.empty()
        );
    }

    @Test
    void throwsExceptionIfFolderDoesNotExist(@TempDir final Path temp) {
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> new EoObjects(temp).objects(),
            "Exception was not thrown when folder does not exist"
        );
    }
}