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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

/**
 * All mojos initialization step.
 * This class is responsible for initializing classloader.
 *
 * @since 0.1
 */
public final class PluginStartup {

    /**
     * Maven project.
     */
    private final MavenProject project;

    /**
     * Constructor.
     * @param project Maven project.
     */
    PluginStartup(final MavenProject project) {
        this.project = project;
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
     *
     * @throws DependencyResolutionRequiredException If a problem happened during loading classes.
     */
    void init() throws DependencyResolutionRequiredException {
        Thread.currentThread().setContextClassLoader(new URLClassLoader(
            this.project.getRuntimeClasspathElements()
                .stream()
                .map(File::new)
                .map(PluginStartup::url).toArray(URL[]::new),
            Thread.currentThread().getContextClassLoader()
        ));
    }

    /**
     * Convert file to URL.
     *
     * @param file File.
     * @return URL.
     */
    private static URL url(final File file) {
        try {
            return file.toURI().toURL();
        } catch (final MalformedURLException exception) {
            throw new IllegalStateException(
                String.format("Can't convert file %s to URL", file),
                exception
            );
        }
    }
}
