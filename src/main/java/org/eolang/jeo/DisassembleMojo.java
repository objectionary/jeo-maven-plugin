/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import com.jcabi.log.Logger;
import java.io.File;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eolang.jeo.representation.asm.DisassembleMode;

/**
 * Disassembles Java bytecode into XMIR representation.
 *
 * <p>This Maven plugin converts compiled Java class files into low-level EO representation
 * (in XMIR format) that contains JVM opcodes and their operands. The resulting XMIR files
 * preserve all bytecode instructions and can be assembled back into executable class files.</p>
 *
 * <p>The plugin supports different disassembly modes to control the level of detail in the
 * output, including debug information such as line numbers and variable names.</p>
 *
 * @since 0.1.0
 */
@Mojo(name = "disassemble", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public final class DisassembleMojo extends AbstractMojo {

    /**
     * Maven project instance.
     * <p>
     * Provides access to project configuration and classpath dependencies required for
     * bytecode analysis and disassembly.
     * </p>
     *
     * @since 0.2.0
     */
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    /**
     * Source directory containing compiled Java class files.
     * <p>
     * This directory should contain {@code .class} files that will be disassembled into
     * XMIR format. Typically points to the project's build output directory.
     * </p>
     *
     * @since 0.2.0
     * @checkstyle MemberNameCheck (6 lines)
     */
    @Parameter(
        property = "jeo.disassemble.sourcesDir",
        defaultValue = "${project.build.outputDirectory}"
    )
    private File sourcesDir;

    /**
     * Target directory for generated XMIR files.
     * <p>
     * All disassembled XMIR files will be written to this directory, preserving the package
     * structure of the original class files. Each class file will be converted to a corresponding
     * XMIR file with {@code .xmir} extension.
     * </p>
     *
     * @since 0.2.0
     * @checkstyle MemberNameCheck (6 lines)
     */
    @Parameter(
        property = "jeo.disassemble.outputDir",
        defaultValue = "${project.build.directory}/generated-sources/jeo-xmir"
    )
    private File outputDir;

    /**
     * Flag to disable the plugin execution.
     * <p>
     * When set to {@code true}, the plugin will skip all processing and exit immediately.
     * This can be useful for conditional builds or troubleshooting.
     * </p>
     *
     * @since 0.2.0
     * @checkstyle MemberNameCheck (6 lines)
     */
    @Parameter(
        property = "jeo.disassemble.disabled",
        defaultValue = "false"
    )
    private boolean disabled;

    /**
     * Disassembly mode controlling the level of detail in output.
     * <p>
     * Supported modes:
     * <ul>
     *   <li>{@code short} - Minimal output with bytecode instructions only (default)</li>
     *   <li>{@code debug} - Include debug information such as line numbers, local variables,
     *       and source file references</li>
     * </ul>
     * </p>
     *
     * @since 0.6.0
     * @checkstyle MemberNameCheck (6 lines)
     */
    @Parameter(
        property = "jeo.disassemble.mode",
        defaultValue = "short"
    )
    private String mode;

    /**
     * Flag to enable XMIR verification after disassembling.
     * <p>
     * When enabled, verifies all generated XMIR files for structural integrity and correctness
     * after disassembly. If any XMIR file is invalid or corrupted, the build process will fail.
     * This verification is disabled by default for performance.
     * </p>
     *
     * @since 0.8.0
     * @checkstyle MemberNameCheck (6 lines)
     */
    @Parameter(
        property = "jeo.disassemble.xmir.verification",
        defaultValue = "false"
    )
    private boolean xmirVerification;

    /**
     * Flag to omit XML comments from generated XMIR files.
     * <p>
     * When enabled, no XML comments will be generated in the XMIR output, which can be
     * useful for production builds where comments are not needed and may reduce file size.
     * When disabled, XML comments will be included to provide debugging information.
     * </p>
     *
     * @since 0.9.0
     * @checkstyle MemberNameCheck (6 lines)
     */
    @Parameter(
        property = "jeo.disassemble.omitComments",
        defaultValue = "false"
    )
    private boolean omitComments;

    /**
     * Set sources directory for testing.
     * @param dir Sources directory
     */
    public void setSourcesDir(final File dir) {
        this.sourcesDir = dir;
    }

    /**
     * Set output directory for testing.
     * @param dir Output directory
     */
    public void setOutputDir(final File dir) {
        this.outputDir = dir;
    }

    /**
     * Set omit comments flag for testing.
     * @param omit Whether to omit comments
     */
    public void setOmitComments(final boolean omit) {
        this.omitComments = omit;
    }

    /**
     * Set Maven project for testing.
     * @param proj Maven project
     */
    public void setProject(final MavenProject proj) {
        this.project = proj;
    }

    /**
     * Set mode for testing.
     * @param mode Disassemble mode
     */
    public void setMode(final String mode) {
        this.mode = mode;
    }

    @Override
    public void execute() throws MojoExecutionException {
        try {
            new PluginStartup(this.project, this.sourcesDir.toPath()).init();
            if (this.disabled) {
                Logger.info(this, "Disassemble mojo is disabled, skipping");
            } else {
                Logger.info(this, "Disassembling is started with mode '%s'", this.mode);
                new Disassembler(
                    this.sourcesDir.toPath(),
                    this.outputDir.toPath(),
                    DisassembleMode.fromString(this.mode),
                    this.omitComments
                ).disassemble();
                if (this.xmirVerification) {
                    Logger.info(this, "Verifying all the XMIR files after disassembling");
                    new XmirFiles(this.outputDir.toPath()).verify();
                } else {
                    Logger.info(
                        this, "XMIR verification after disassembling is disabled, skipping"
                    );
                }
            }
        } catch (final DependencyResolutionRequiredException exception) {
            throw new MojoExecutionException(
                String.format(
                    "Failed to transpile bytecode to EO, from '%s' to '%s'",
                    this.sourcesDir.toPath(),
                    this.outputDir.toPath()
                ),
                exception
            );
        }
    }
}
