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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eolang.jeo.improvement.ImprovementBytecodeFootprint;
import org.eolang.jeo.improvement.ImprovementDistilledObjects;
import org.eolang.jeo.improvement.ImprovementXmirFootprint;
import org.eolang.jeo.improvement.ImprovementLogged;
import org.eolang.jeo.improvement.ImprovementSet;

/**
 * Default optimization mojo.
 * This mojo transforms bytecode into eo code, then applies all improvements and
 * converts it back to bytecode.
 *
 * @since 0.1.0
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
     * Project default target directory.
     * @since 0.1.0
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources")
    private File generated;

    /**
     * The main entry point of the plugin.
     *
     * @throws MojoExecutionException If some execution problem arises
     */
    public void execute() throws MojoExecutionException {
        try {
            new Optimization(
                this.classes.toPath(),
                new ImprovementSet(
                    new ImprovementLogged(),
                    new ImprovementDistilledObjects(),
                    new ImprovementXmirFootprint(this.generated.toPath()),
                    new ImprovementBytecodeFootprint(this.classes.toPath())
                )
            ).apply();
        } catch (final IllegalStateException | IOException exception) {
            throw new MojoExecutionException(exception);
        }
    }
}
