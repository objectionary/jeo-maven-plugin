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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

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
    public void execute() {
        Logger.info(this, "Unrolling PHI/UNPHI transformations");
        long start = System.currentTimeMillis();
        final long count = new Unroller(this.sourcesDir.toPath(), this.outputDir.toPath()).unroll();
        Logger.info(
            this,
            "Total %d PHI/UNPHI transformations were unrolled in %[ms]s",
            count,
            System.currentTimeMillis() - start
        );
    }
}
