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
import com.jcabi.xml.XML;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.Translation;
import org.eolang.jeo.Representation;
import org.eolang.jeo.representation.XmirRepresentation;
import org.eolang.jeo.representation.JavaName;

/**
 * Footprint of the EO's.
 *
 * @since 0.1.0
 */
public final class TranslationXmirFootprint implements Translation {

    /**
     * Where to save the EO.
     */
    private final Path target;

    /**
     * Constructor.
     *
     * @param home Where to save the EO.
     */
    public TranslationXmirFootprint(final Path home) {
        this.target = home;
    }

    @Override
    public Collection<Representation> apply(
        final Collection<? extends Representation> representations
    ) {
        Logger.info(this, "Disassembling .class files to %[file]s", this.target);
        final List<Representation> res = representations.stream()
            .map(this::transform)
            .collect(Collectors.toList());
        Logger.info(this, "Total %d .class files were disassembled", res.size());
        return res;
    }

    /**
     * Try to save XMIR to the target folder and return new representation.
     *
     * @param representation Representation to save.
     * @return New representation with source attached to the saved file.
     * @todo #431:30min Measure the disassembling time and print it to logs.
     *  The disassembling time should be measured in milliseconds and printed to logs.
     *  Moreover, we have to add one more log entry that would print the path of the file
     *  being disassembled. The entire log should look like this:
     *  "Disassembling file.class (5kb)....".
     *  "Disassembled file.xmir (6kb) in 100ms".
     */
    private Representation transform(final Representation representation) {
        final String name = new JavaName(representation.details().name()).decode();
        final Path path = this.target
            .resolve(String.format("%s.xmir", name.replace('/', File.separatorChar)));
        try {
            Files.createDirectories(path.getParent());
            final XML xmir = representation.toEO();
            Files.write(
                path,
                xmir.toString().getBytes(StandardCharsets.UTF_8)
            );
            Logger.info(
                this,
                "%s disassembled to %[file]s (%[size]s)",
                representation.details().source(),
                path,
                Files.size(path)
            );
            return new XmirRepresentation(xmir, path.getFileName().toString());
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't save XML to %s", path),
                exception
            );
        }
    }
}
