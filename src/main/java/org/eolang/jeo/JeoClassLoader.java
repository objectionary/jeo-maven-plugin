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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
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
    JeoClassLoader(final ClassLoader parent, final List<String> classes) {
        this(parent, prestructor(classes));
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
    private static Map<String, byte[]> prestructor(final List<String> classes) {
        return classes.stream()
            .parallel()
            .map(Paths::get)
            .filter(Files::exists)
            .flatMap(JeoClassLoader::clazzes)
            .collect(Collectors.toMap(MapEntry::getKey, MapEntry::getValue));
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
