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
 * Converts bytecode to EO.
 * In other words, it disassembles bytecode to low-level EO representation that contains
 * opcodes and their values.
 * The mojo that converts bytecode to EO only.
 * It does not apply any improvements. It does not convert EO to bytecode back.
 *
 * @since 0.1.0
 */
@Mojo(name = "disassemble", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public final class DisassembleMojo extends AbstractMojo {

    /**
     * Maven project.
     *
     * @since 0.2
     */
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    /**
     * Source directory.
     * Where to take classes from.
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
     * Target directory.
     * Where to save EO representations to.
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
     * Whether the plugin is disabled.
     * If it's disabled, then it won't do anything.
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
     * Mode in which to disassemble the bytecode.
     * Can be either 'plain' or 'debug':
     * - 'short' mode will disassemble the bytecode without any additional information.
     * - 'debug' mode will disassemble the bytecode with additional information like line numbers.
     * Default is 'short'.
     *
     * @since 0.6
     * @checkstyle MemberNameCheck (6 lines)
     */
    @Parameter(
        property = "jeo.disassemble.mode",
        defaultValue = "short"
    )
    private String mode;

    /**
     * Xmir verification after disassembling.
     * After disassembling, we verify all the xmir files.
     * If any of them are invalid or corrupted, we stop the process.
     * If you want to run this verification, set this parameter to true.
     *
     * @since 0.8
     * @checkstyle MemberNameCheck (6 lines)
     */
    @Parameter(
        property = "jeo.assemble.xmir.verification",
        defaultValue = "false"
    )
    private boolean xmirVerification;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            new PluginStartup(this.project, this.sourcesDir.toPath()).init();
            if (this.disabled) {
                Logger.info(this, "Disassemble mojo is disabled. Skipping.");
            } else {
                Logger.info(this, "Disassembling is started with mode '%s'", this.mode);
                new Disassembler(
                    this.sourcesDir.toPath(),
                    this.outputDir.toPath(),
                    DisassembleMode.fromString(this.mode)
                ).disassemble();
                if (this.xmirVerification) {
                    Logger.info(this, "Verifying all the xmir files after disassembling.");
                    new XmirFiles(this.outputDir.toPath()).verify();
                } else {
                    Logger.info(
                        this, "Xmir verification after disassembling is disabled. Skipping."
                    );
                }
            }
        } catch (final DependencyResolutionRequiredException exception) {
            throw new MojoExecutionException(
                String.format(
                    "Can't transpile bytecode from '%s' to EO. Output directory: '%s'.",
                    this.sourcesDir.toPath(),
                    this.outputDir.toPath()
                ),
                exception
            );
        }
    }
}
