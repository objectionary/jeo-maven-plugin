/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
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
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

/**
 * All mojo's initialization step.
 * This class is responsible for initializing classloader.
 *
 * @since 0.1
 */
public final class PluginStartup {

    /**
     * All the folders with classes.
     */
    private final Collection<String> folders;

    /**
     * Constructor.
     * @param project Maven project.
     * @param additional Additional folders with classes.
     * @throws DependencyResolutionRequiredException If a problem happened during loading classes.
     */
    PluginStartup(
        final MavenProject project, final Path... additional
    ) throws DependencyResolutionRequiredException {
        this(
            Stream.concat(
                Stream.concat(
                    project.getRuntimeClasspathElements().stream(),
                    project.getCompileClasspathElements().stream()
                ),
                Stream.concat(
                    project.getTestClasspathElements().stream(),
                    Arrays.stream(additional).map(Path::toString)
                )
            ).collect(Collectors.toSet())
        );
    }

    /**
     * Constructor.
     * @param folders Folders with classes.
     */
    PluginStartup(final String... folders) {
        this(Arrays.asList(folders));
    }

    /**
     * Constructor.
     * @param folders Folders with classes.
     */
    private PluginStartup(final Collection<String> folders) {
        this.folders = folders;
    }

    /**
     * Initialize classloader.
     * This method is important to load classes that were compiled on the previous maven
     * phases. Since the jeo plugin works on the 'process-classes' phase, it might
     * see classes that were compiled on the 'compile' phase.
     * We need to have all these classes in the classpath to be able to load them during
     * the transformation phase.
     * We need this to solve the problem with computing maxs in ASM library:
     * - https://gitlab.ow2.org/asm/asm/-/issues/317918
     * - https://stackoverflow.com/questions/11292701/error-while-instrumenting-class-files-asm-classwriter-getcommonsuperclass
     */
    void init() {
        Logger.info(
            this,
            String.format(
                "Trying to load classes for bytecode verification from %s",
                this.folders.stream().collect(Collectors.joining(", ", "[", "]"))
            )
        );
        Thread.currentThread().setContextClassLoader(
            new JeoClassLoader(
                Thread.currentThread().getContextClassLoader(),
                this.folders
            )
        );
    }

}
