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
import java.io.IOException;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eolang.jeo.representation.BytecodeTransformation;

/**
 * Converts bytecode to EO.
 * The mojo that converts bytecode to EO only.
 * It does not apply any improvements. It does not convert EO to bytecode back.
 *
 * @since 0.1.0
 */
@Mojo(name = "bytecode-to-eo", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public final class BytecodeToEoMojo extends AbstractMojo {

    /**
     * Maven project.
     *
     * @since 0.2
     */
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    /**
     * Project compiled classes.
     *
     * @since 0.1.0
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}")
    private File classes;

    /**
     * Project default target directory.
     *
     * @since 0.1.0
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources")
    private File generated;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            new PluginStartup(this.project).init();
            new BytecodeTransformation(this.classes.toPath(), this.generated.toPath()).transpile();
        } catch (final IOException | DependencyResolutionRequiredException exception) {
            throw new MojoExecutionException(
                String.format(
                    "Can't transpile bytecode from '%s' to EO. Output directory: '%s'.",
                    this.classes.toPath(),
                    this.generated.toPath()
                ),
                exception
            );
        }
    }
}
