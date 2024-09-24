/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eolang.jeo.representation.CanonicalXmir;

/**
 * This mojo unrolls all the changes made by PHI/UNPHI transformations.
 * In other words, it makes XMIR understandable by jeo-maven-plugin after PHI/UNPHI transformations.
 * @since 0.6
 */
@Mojo(name = "unroll-phi", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public final class UnrollMojo extends AbstractMojo {

    /**
     * Source directory.
     *
     * @since 0.6
     * @checkstyle MemberNameCheck (6 lines)
     */
    @Parameter(
        property = "jeo.unroll-phi.sourcesDir",
        defaultValue = "${project.build.directory}/generated-sources/jeo-xmir"
    )
    private File sourcesDir;

    /**
     * Target directory.
     *
     * @since 0.6
     * @checkstyle MemberNameCheck (6 lines)
     */
    @Parameter(
        property = "jeo.unroll-phi.outputDir",
        defaultValue = "${project.build.directory}/generated-sources/jeo-unrolled"
    )
    private File outputDir;

    @Override
    public void execute() throws MojoFailureException {
        try (Stream<Path> xmirs = Files.walk(this.sourcesDir.toPath())) {
            xmirs.filter(UnrollMojo::isXmir).forEach(this::unroll);
        } catch (final IOException exception) {
            throw new MojoFailureException(
                String.format(
                    "Failed to unroll XMIR files from '%s' directory to '%s'",
                    this.sourcesDir,
                    this.outputDir
                ),
                exception
            );
        }
    }

    /**
     * Unrolls the XMIR file.
     * @param path The path to the XMIR file.
     */
    private void unroll(final Path path) {
        try {
            final Path output = this.outputDir.toPath()
                .resolve(this.sourcesDir.toPath().relativize(path));
            Logger.info(this, "Unrolling XMIR file '%s' to '%s'", path, output);
            Files.createDirectories(output.getParent());
            Files.write(
                output,
                new CanonicalXmir(path)
                    .plain()
                    .toString()
                    .getBytes(StandardCharsets.UTF_8)
            );
        } catch (final FileNotFoundException exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to unroll XMIR file '%s', most probably the file does not exist",
                    path
                ),
                exception
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to unroll XMIR file '%s', most probably an I/O error occurred",
                    path
                ),
                exception
            );
        }
    }

    /**
     * Checks if the file is an XMIR file.
     * @param path The path to the file.
     * @return True if the file is an XMIR file, false otherwise.
     */
    private static boolean isXmir(final Path path) {
        return Files.isRegularFile(path) && path.toString().endsWith(".xmir");
    }
}
