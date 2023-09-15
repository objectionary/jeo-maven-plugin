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

import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Converts EO to bytecode.
 * The mojo that converts EO to bytecode only.
 * It does not apply any improvements.
 *
 * @since 0.1.0
 * @todo #59:90min Duplication between EoToBytecode and Optimization.
 *  Both classes have the same code. We have to extract the common code to the separate class.
 *  The class should be able to convert EO to bytecode and save result to the target folder.
 *  When it is done, remove that puzzle.
 */
@Mojo(name = "eo-to-bytecode", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public final class EoToBytecodeMojo extends AbstractMojo {

    /**
     * Project compiled classes.
     * @since 0.1.0
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}")
    private File classes;

    /**
     * Project default target directory.
     * @since 0.1.0
     */
    @Parameter(defaultValue = "${project.build.directory}")
    private File target;

    @Override
    public void execute() {
        new EoObjects(this.target.toPath())
            .objects()
            .forEach(this::recompile);
    }

    /**
     * Recompile the Intermediate Representation.
     * @param representation Intermediate Representation to recompile.
     */
    private void recompile(final Representation representation) {
        final String name = representation.name();
        try {
            final byte[] bytecode = representation.toBytecode();
            final String[] subpath = name.split("\\.");
            subpath[subpath.length - 1] = String.format("%s.class", subpath[subpath.length - 1]);
            final Path path = Paths.get(this.classes.toString(), subpath);
            Logger.info(
                this,
                "Recompiling '%s', bytecode instance '%s', bytes to save '%s'",
                path,
                representation.getClass(),
                bytecode.length
            );
            Files.createDirectories(path.getParent());
            Files.write(path, bytecode);
            Logger.info(
                this,
                "%s was recompiled successfully.",
                path.getFileName().toString()
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(String.format("Can't recompile '%s'", name), exception);
        }
    }
}
