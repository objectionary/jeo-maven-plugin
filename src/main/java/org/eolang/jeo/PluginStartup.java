/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
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
