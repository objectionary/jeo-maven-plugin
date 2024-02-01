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
 * Test case for {@link TranslationXmirFootprint}.
 *
 * @since 0.1.0
 */
final class TranslationXmirFootprintTest {

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
    void savesXml(@TempDir final Path temp) {
        final TranslationXmirFootprint footprint = new TranslationXmirFootprint(temp);
        footprint.apply(this.objects);
        MatcherAssert.assertThat(
            "XML file was not saved",
            temp.resolve(this.expected).toFile(),
            FileMatchers.anExistingFile()
        );
    }

    @Test
    void overwritesXml(@TempDir final Path temp) {
        final TranslationXmirFootprint footprint = new TranslationXmirFootprint(temp);
        footprint.apply(this.objects);
        footprint.apply(this.objects);
        MatcherAssert.assertThat(
            "XML file was not successfully overwritten",
            temp.resolve(this.expected).toFile(),
            FileMatchers.anExistingFile()
        );
    }
}
