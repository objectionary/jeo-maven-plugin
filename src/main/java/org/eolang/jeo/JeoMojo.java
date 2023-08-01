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
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal which touches a timestamp file.
 *
 * @since 0.1.0
 * @todo #9:90min Implement the optimization skeleton.
 *  The optimization skeleton should be implemented without details. Just a sketch.
 *  The skeleton should contain the next steps:
 *  - Transpile all bytecode files to the eolang files (XMIR I guess).
 *  - Invoke some XMIR optimizations (dummy for now).
 *  - Transpile all XMIR files back to the bytecode.
 */
@Mojo(name = "optimize", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public final class JeoMojo extends AbstractMojo {

    /**
     * Project compiled classes.
     * @since 0.1.0
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}")
    private File classes;

    /**
     * The main entry point of the plugin.
     *
     * @throws MojoExecutionException If some execution problem arises
     */
    public void execute() throws MojoExecutionException {
        try {
            this.tryExecute();
        } catch (final IllegalStateException | IOException exception) {
            throw new MojoExecutionException(exception);
        }
    }

    /**
     * Try to execute the plugin.
     * @throws IOException If some I/O problem arises
     */
    private void tryExecute() throws IOException {
        Logger.info(this, "The first dummy implementation of jeo-maven-plugin");
        this.bytecode().forEach(
            path -> Logger.info(this, "Optimization candidate: %s", path.getFileName())
        );
        Logger.info(this, "jeo optimization is finished successfully!");
    }

    /**
     * Find all bytecode files.
     * @return Collection of bytecode files
     * @throws IOException If some I/O problem arises
     */
    private Collection<Path> bytecode() throws IOException {
        if (Objects.isNull(this.classes)) {
            throw new IllegalStateException(
                "The classes directory is not set, jeo-maven-plugin does not know where to look for classes."
            );
        }
        try (Stream<Path> walk = Files.walk(this.classes.toPath())) {
            return walk.filter(path -> path.toString().endsWith(".class"))
                .collect(Collectors.toList());
        }
    }
}
