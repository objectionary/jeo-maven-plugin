/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import org.eolang.jeo.representation.XmirRepresentation;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.hamcrest.MatcherAssert;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link BachedTranslator}.
 *
 * @since 0.1.0
 */
final class BachedTranslatorTest {

    /**
     * Representations to test.
     */
    private final Collection<Representation> objects = Collections.singleton(
        new XmirRepresentation(
            new BytecodeClass("org/eolang/jeo/Application").xml()
        )
    );

    /**
     * Where the XML file is expected to be saved.
     */
    private final Path expected = Paths.get("")
        .resolve("org")
        .resolve("eolang")
        .resolve("jeo")
        .resolve("Application.xmir");

    @Test
    void savesXml(@TempDir final Path temp) throws IOException {
        final Path clazz = temp.resolve("Application.xml");
        Files.write(
            clazz,
            new BytecodeClass("org/eolang/jeo/Application")
                .xml()
                .toString()
                .getBytes(StandardCharsets.UTF_8)
        );
        new BachedTranslator(new Disassemble(temp))
            .apply(Collections.singleton(new XmirRepresentation(clazz)));
        MatcherAssert.assertThat(
            "XML file was not saved",
            temp.resolve(this.expected).toFile(),
            FileMatchers.anExistingFile()
        );
    }

    @Test
    void overwritesXml(@TempDir final Path temp) {
        final BachedTranslator footprint = new BachedTranslator(
            new Disassemble(temp)
        );
        footprint.apply(this.objects);
        footprint.apply(this.objects);
        MatcherAssert.assertThat(
            "XML file was not successfully overwritten",
            temp.resolve(this.expected).toFile(),
            FileMatchers.anExistingFile()
        );
    }

    @Test
    void assemblesSuccessfully(@TempDir final Path temp) {
        final String fake = "jeo/xmir/Fake";
        new BachedTranslator(new Assemble(temp)).apply(
            Collections.singleton(new XmirRepresentation(new BytecodeClass(fake).xml()))
        );
        MatcherAssert.assertThat(
            String.format(
                "Bytecode file was not saved for the representation with the name '%s'",
                fake
            ),
            temp.resolve("jeo")
                .resolve("xmir")
                .resolve("Fake.class")
                .toFile(),
            FileMatchers.anExistingFile()
        );
    }
}
