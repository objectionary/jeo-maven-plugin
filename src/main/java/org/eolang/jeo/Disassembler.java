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

import com.jcabi.log.Logger;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * This class disassembles the project's compiled classes.
 * It is used to transpile the project's compiled bytecode classes into EO.
 *
 * @since 0.1.0
 */
public class Disassembler {

    /**
     * Project compiled classes.
     */
    private final Path classes;

    /**
     * Where to save decompiled classes.
     */
    private final Path target;

    /**
     * Constructor.
     *
     * @param classes Project compiled classes.
     * @param target Project default target directory.
     */
    public Disassembler(
        final Path classes,
        final Path target
    ) {
        this.classes = classes;
        this.target = target;
    }

    /**
     * Disassemble all bytecode files.
     */
    public void disassemble() {
        final String process = "Disassembling";
        final String disassembled = "disassembled";
        final Stream<? extends Representation> stream = new LoggedTranslator(
            process,
            disassembled,
            this.classes,
            this.target,
            new BachedTranslator(
                new LoggedTranslation(
                    process,
                    disassembled,
                    new Disassemble(this.target)
                )
            )
        ).apply(new BytecodeRepresentations(this.classes).all());
        stream.forEach(
            terminated -> Logger.info(
                this, "Dissembling of '%s' finished successfully.", terminated.details().name()
            )
        );
        stream.close();
    }
}
