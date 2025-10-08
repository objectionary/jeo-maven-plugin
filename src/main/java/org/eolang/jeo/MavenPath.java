/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * Maven file path resolver.
 * Substitutes ${} expressions with concrete values.
 * This class was created to mitigate the following issue:
 * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1373">1373</a>
 * We could have use ${basedir}/target/classes instead of ${project.build.outputDirectory}
 * but it is not guaranteed that output directory is always 'target/classes' and the basedir
 * might be different from '.'. Thus, we just handle the case when we run plugin in the folder
 * with pom.xml file.
 * @since 0.15.0
 */
public final class MavenPath {

    /**
     * Pattern for project basedir.
     */
    private static final Pattern PROJ_BASEDIR = Pattern.compile(
        "${project.basedir}", Pattern.LITERAL
    );

    /**
     * Pattern for basedir.
     */
    private static final Pattern BASEDIR = Pattern.compile("${basedir}", Pattern.LITERAL);

    /**
     * Pattern for output directory.
     */
    private static final Pattern OUT_DIR = Pattern.compile(
        "${project.build.outputDirectory}",
        Pattern.LITERAL
    );

    /**
     * Pattern for build directory.
     */
    private static final Pattern BUILD_DIR = Pattern.compile(
        "${project.build.directory}",
        Pattern.LITERAL
    );

    /**
     * The file path.
     */
    private final File path;

    /**
     * Ctor.
     * @param path The file path
     */
    public MavenPath(final File path) {
        this.path = path;
    }

    /**
     * Resolve the path.
     * @return The resolved path
     */
    public Path resolve() {
        String res = this.path.toPath().toString();
        res = MavenPath.PROJ_BASEDIR.matcher(res).replaceAll(".");
        res = MavenPath.BASEDIR.matcher(res).replaceAll(".");
        res = MavenPath.OUT_DIR.matcher(res).replaceAll("target/classes");
        res = MavenPath.BUILD_DIR.matcher(res).replaceAll("target");
        return Paths.get(res);
    }
}
