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
package org.eolang.jeo.improvement;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import org.eolang.jeo.DisassembleMojo;
import org.eolang.jeo.Improvement;
import org.eolang.jeo.Representation;
import org.eolang.jeo.representation.JavaName;
import org.eolang.parser.xmir.Xmir;

/**
 * It's not actually an improvement.
 * It's just a class that prints all the generated .xmir files as .eo files for convenience.
 *
 * @since 0.1
 * @todo #215:60min Apply ImprovementEoFootprint.
 * Currently we don't use ImprovementEoFootprint. We have to add it to the next classes:
 * - {@link DisassembleMojo}
 * - {@link org.eolang.jeo.JeoMojo}
 * Don't forget to add checks for integration tests.
 */
public final class ImprovementEoFootprint implements Improvement {

    /**
     * Where to save the EO.
     */
    private final Path target;

    /**
     * Constructor.
     *
     * @param generated Where to save the EO, usually it's target/generated-sources folder.
     */
    public ImprovementEoFootprint(final Path generated) {
        this.target = generated;
    }

    @Override
    public Collection<? extends Representation> apply(
        final Collection<? extends Representation> representations
    ) {
        Logger.info(this, "Writing .eo files to %[file]s", this.folder());
        representations.forEach(this::saveEo);
        return representations;
    }

    /**
     * Save the EO representation into the 'target/generated-sources/eo' folder.
     *
     * @param representation EO representation as XMIR.
     */
    private void saveEo(final Representation representation) {
        final String name = new JavaName(representation.details().name()).decode();
        final Path path = this.folder()
            .resolve(String.format("%s.eo", name));
        try {
            Files.createDirectories(path.getParent());
            Files.write(
                path,
                new Xmir.Default(representation.toEO()).toEO().getBytes(StandardCharsets.UTF_8)
            );
            Logger.info(
                this,
                "%s represented as %[file]s (%[size]s)",
                representation.details().source(),
                path,
                Files.size(path)
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't save %s representation into %s",
                    name,
                    path
                ),
                exception
            );
        }
    }

    /**
     * Get the folder where to save the EO.
     *
     * @return The folder where to save the EO.
     */
    private Path folder() {
        return this.target.resolve("eo");
    }
}
