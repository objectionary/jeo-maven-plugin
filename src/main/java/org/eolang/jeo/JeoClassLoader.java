/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.cactoos.map.MapEntry;

/**
 * JEO class loader.
 * This classloader reads classes folder, uploads these classes into a memory
 * and then loads them.
 * This happens before any transformations are applied.
 * Preloading allows safe classes validation.
 * Validator loads "old" classes and uses them for the validation of the "newly" generated classes.
 * Moreover, by using {@link JeoClassLoader}, we can guarantee that the classes loaded
 * before any transformations are correct.
 * If we use any other {@link ClassLoader} implementation it leads to flaky tests as
 * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/672">issue 672</a> shows.
 * @since 0.6
 */
public final class JeoClassLoader extends ClassLoader {

    /**
     * Class extension.
     */
    private static final String CLASS = ".class";

    /**
     * Classes.
     */
    private final Map<String, byte[]> classes;

    /**
     * Cache.
     */
    private final Map<String, Class<?>> cache;

    /**
     * Constructor.
     * @param parent Parent class loader.
     * @param classes Classes as file paths.
     */
    JeoClassLoader(final ClassLoader parent, final Collection<String> classes) {
        this(parent, JeoClassLoader.prestructor(classes));
    }

    /**
     * Constructor.
     * @param parent Parent class loader.
     * @param classes Classes as byte arrays.
     */
    private JeoClassLoader(final ClassLoader parent, final Map<String, byte[]> classes) {
        super(parent);
        this.classes = classes;
        this.cache = new HashMap<>(0);
    }

    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        try {
            final Class<?> result;
            if (this.cache.containsKey(name)) {
                result = this.cache.get(name);
            } else if (this.classes.containsKey(name)) {
                final Class<?> created = this.defineClass(
                    name, this.classes.get(name), 0, this.classes.get(name).length
                );
                this.cache.put(name, created);
                result = created;
            } else {
                result = super.loadClass(name);
            }
            return result;
        } catch (final ClassNotFoundException exception) {
            throw new ClassNotFoundException(
                String.format(
                    "Class not found: '%s', known classes: '%s'",
                    name,
                    this.classes.keySet()
                ), exception
            );
        }
    }

    /**
     * Construct a map of classes.
     * @param classes File paths.
     * @return Map of classes.
     */
    private static Map<String, byte[]> prestructor(final Collection<String> classes) {
        return classes.stream()
            .parallel()
            .map(Paths::get)
            .filter(Files::exists)
            .flatMap(JeoClassLoader::clazzes)
            .collect(
                Collectors.toMap(MapEntry::getKey, MapEntry::getValue, (first, second) -> first)
            );
    }

    /**
     * Check if the path is a class.
     * @param path Path to check.
     * @return True if the path is a class.
     */
    private static boolean isClass(final Path path) {
        return Files.isRegularFile(path)
            && path.getFileName().toString().endsWith(JeoClassLoader.CLASS);
    }

    /**
     * Find classes in the root folder.
     * @param root Root folder.
     * @return Stream of classes with their names.
     */
    private static Stream<MapEntry<String, byte[]>> clazzes(final Path root) {
        try {
            return Files.walk(root)
                .filter(JeoClassLoader::isClass)
                .map(clazz -> JeoClassLoader.entry(root, clazz));
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Failed to walk through the folder '%s'", root),
                exception
            );
        }
    }

    /**
     * Create a class entry.
     * @param root Root folder.
     * @param file File of the class.
     * @return Class entry.
     */
    private static MapEntry<String, byte[]> entry(final Path root, final Path file) {
        return new MapEntry<>(
            root.relativize(file)
                .toString()
                .replace(File.separatorChar, '.')
                .replace(JeoClassLoader.CLASS, ""),
            new UncheckedBytes(new BytesOf(file.toAbsolutePath())).asBytes()
        );
    }
}
