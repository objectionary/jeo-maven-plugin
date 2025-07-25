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
import org.eolang.jeo.representation.asm.DisassembleParams;

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
     * Flag to omit detailed bytecode listings in generated XMIR.
     * <p>
     * When enabled, the {@code <listing>} element in XMIR files will not contain bytecode listing.
     * This reduces file size and improves readability in production environments where detailed
     * bytecode output is not needed. When disabled, full bytecode listings are included
     * for debugging purposes.
     * </p>
     *
     * @since 0.11.0
     * @checkstyle MemberNameCheck (6 lines)
     */
    @Parameter(
        property = "jeo.disassemble.omitListings",
        defaultValue = "true"
    )
    private boolean omitListings;

    /**
     * Flag to omit XML comments in generated XMIR files.
     * <p>
     * When enabled, no comments will be generated in the XMIR output, which can be
     * useful for production builds where comments are not needed and may reduce file size.
     * When disabled, XML comments will be included to provide debugging information.
     * </p>
     *
     * @since 0.11.0
     * @checkstyle MemberNameCheck (6 lines)
     */
    @Parameter(
        property = "jeo.disassemble.omitComments",
        defaultValue = "true"
    )
    private boolean omitComments;

    /**
     * Flag to enable pretty-printing of XMIR files.
     * <p>
     *     When enabled, the generated XMIR files will be formatted with indentation (2 spaces) and
     *     line breaks for better readability.
     *     This is useful for development and debugging purposes.
     *     By default, pretty-printing is enabled, but it's best to disable it for large
     *     projects or production builds to reduce file size and improve performance.
     * </p>
     * @since 0.11.0
     * @checkstyle MemberNameCheck (6 lines)
     */
    @Parameter(
        property = "jeo.disassemble.prettyXmir",
        defaultValue = "true"
    )
    private boolean prettyXmir;

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

    @Override
    public void execute() throws MojoExecutionException {
        try {
            new PluginStartup(this.project, this.sourcesDir.toPath()).init();
            if (this.disabled) {
                Logger.info(this, "Disassemble mojo is disabled, skipping");
            } else {
                final boolean listings = !this.omitListings;
                final boolean comments = !this.omitComments;
                Logger.info(
                    this,
                    "Disassembling is started with mode '%s' (with listings = '%b', comments = '%b')",
                    this.mode,
                    listings,
                    comments
                );
                new Disassembler(
                    this.sourcesDir.toPath(),
                    this.outputDir.toPath(),
                    new DisassembleParams(
                        DisassembleMode.fromString(this.mode),
                        listings,
                        this.prettyXmir,
                        comments
                    )
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
