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
package org.eolang.jeo.representation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import org.eolang.jeo.BytecodeClasses;
import org.eolang.jeo.improvement.ImprovementEoFootprint;
import org.eolang.jeo.improvement.ImprovementSet;
import org.eolang.jeo.improvement.ImprovementXmirFootprint;

/**
 * Transpilation of the bytecode to the EO.
 *
 * @since 0.1.0
 */
public class BytecodeTransformation {

    /**
     * Project compiled classes.
     */
    private final Path classes;

    /**
     * Project default target directory.
     */
    private final Path target;

    /**
     * Constructor.
     * @param classes Project compiled classes.
     * @param target Project default target directory.
     */
    public BytecodeTransformation(
        final Path classes,
        final Path target
    ) {
        this.classes = classes;
        this.target = target;
    }

    /**
     * Transpile all bytecode files.
     * @throws IOException If some I/O problem arises.
     */
    public void transpile() throws IOException {
        new ImprovementSet(
            new ImprovementEoFootprint(this.target),
            new ImprovementXmirFootprint(this.target)
        ).apply(new BytecodeClasses(this.classes).bytecode());
    }
}
