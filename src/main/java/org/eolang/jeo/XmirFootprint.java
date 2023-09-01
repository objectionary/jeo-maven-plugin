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

import com.jcabi.xml.XML;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;

/**
 * Footprint of the Xmir.
 *
 * @since 0.1.0
 */
final class XmirFootprint implements Boost {

    /**
     * Where to save the Xmir.
     */
    private final Path target;

    XmirFootprint(final Path home) {
        this.target = home;
    }

    @Override
    public Collection<IR> apply(final Collection<IR> representations) {
        representations.stream().map(IR::toEO).forEach(this::tryToSave);
        return representations;
    }

    /**
     * Try to save XML to the target folder.
     * @param xml XML to save.
     * @todo #36:90min Hardcoded XMIR path.
     *   The XMIR path is hardcoded in the tryToSave method.
     *   It should be flexible and related to the Java class name.
     *   For example, if the class is org.eolang.jeo.Dummy,
     *   the XMIR path should be org/eolang/jeo/Dummy.xmir.
     *   if the class is org.eolang.jeo.Fake, the XMIR path should be
     *   org/eolang/jeo/Fake.xmir and so on.
     */
    private void tryToSave(final XML xml) {
        final Path path = this.target.resolve("jeo")
            .resolve("xmir")
            .resolve("org")
            .resolve("eolang")
            .resolve("jeo")
            .resolve("Application.xmir");
        try {
            Files.createDirectories(path.getParent());
            Files.write(
                path,
                xml.toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE_NEW
            );
        } catch (IOException ex) {
            throw new IllegalStateException(
                String.format("Can't save XML to %s", path),
                ex
            );
        }
    }
}
