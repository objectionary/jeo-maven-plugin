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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal which touches a timestamp file.
 *
 * @since 0.1.0
 * @todo #5:30min Add logging for the plugin entry point.
 *  The plugin should log the entry point of the plugin. Maybe it should log the
 *  list of already compiled classes. When it will be done, remove that
 *  puzzle.
 */
@Mojo(name = "optimize", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public final class JeoMojo extends AbstractMojo {

    @Parameter(property = "${project.build.outputDirectory}")
    private File classes;

    /**
     * The main entry point of the plugin.
     */
    public void execute() {
        try {
            this.tryExecute();
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Try to execute the plugin.
     * @throws IOException If some I/O problem arises
     */
    private void tryExecute() throws IOException {
        Logger.info(this, "The first dummy implementation of jeo-maven-plugin");
        this.bytecode().forEach(path -> Logger.info(this, "Found class: %s", path));
        Logger.info(this, "jeo optimization is finished successfully!");
    }

    /**
     * Find all bytecode files.
     * @return Collection of bytecode files
     * @throws IOException If some I/O problem arises
     */
    private Collection<Path> bytecode() throws IOException {
        try (final Stream<Path> walk = Files.walk(this.classes.toPath())) {
            return walk.filter(path -> path.toString().endsWith(".class"))
                .collect(Collectors.toList());
        }
    }
}
