/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.io.File;
import java.nio.file.Paths;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test case for {@link MavenPath}.
 * @since 0.15.0
 */
final class MavenPathTest {

    @ParameterizedTest
    @CsvSource({
        "src/test/resources, src/test/resources",
        "${project.basedir}/src/test/resources, ./src/test/resources",
        "${basedir}/src/test/resources, ./src/test/resources",
        "${project.build.outputDirectory}/extra, target/classes/extra",
        "${project.build.directory}/extra, target/extra"

    })
    void resolvesPath(final String input, final String expected) {
        MatcherAssert.assertThat(
            "Path resolved correctly",
            new MavenPath(new File(input)).resolve(),
            Matchers.equalTo(Paths.get(expected))
        );
    }
}
