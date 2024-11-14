/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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
import org.eolang.jeo.representation.PrefixedName;

public final class AssembleTrans implements FileTransformation {

    private final Path target;
    private final Representation representation;

    AssembleTrans(final Path target, final Representation representation) {
        this.target = target;
        this.representation = representation;
    }

    @Override
    public Path from() {
        return this.representation.details().source().orElseThrow(
            () -> new IllegalStateException(
                String.format(
                    "Source is not defined for assembling '%s'",
                    this.representation.details()
                )
            )
        );
    }

    @Override
    public Path to() {
        final String name = new PrefixedName(this.representation.details().name()).decode();
        final String[] subpath = name.split("\\.");
        subpath[subpath.length - 1] = String.format("%s.class", subpath[subpath.length - 1]);
        return Paths.get(this.target.toString(), subpath);
    }

    @Override
    public byte[] transform() {
        return this.representation.toBytecode().bytes();
    }
}
