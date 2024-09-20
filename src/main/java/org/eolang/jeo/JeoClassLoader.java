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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.cactoos.io.ResourceOf;
import org.cactoos.map.MapEntry;

/**
 * JEO class loader.
 */
public final class JeoClassLoader extends ClassLoader {

    private final Map<String, byte[]> classes;

    public JeoClassLoader(final ClassLoader parent, final List<String> classes) {
        this(parent, prestructor(classes));
    }

    public JeoClassLoader(final ClassLoader parent, final Map<String, byte[]> classes) {
        super(parent);
        this.classes = classes;
    }

    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        try {
            if (this.classes.containsKey(name)) {
                return this.defineClass(
                    name, this.classes.get(name), 0, this.classes.get(name).length);
            } else {
                return super.loadClass(name);
            }
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

    private static Map<String, byte[]> prestructor(List<String> classes) {
        return classes.stream().flatMap(
            c -> {
                final Path p = Paths.get(c);
                if (!Files.exists(p)) {
                    return Stream.empty();
                }
                System.out.println("PATH: " + p);
                try {
                    return Files.walk(p)
                        .peek(file -> System.out.println("FILE: " + file))
                        .filter(Files::isRegularFile)
                        .filter(f -> f.getFileName().toString().endsWith(".class"))
                        .map(f -> file(p, f));
                } catch (final IOException exception) {
                    throw new RuntimeException(exception);
                }
            }
        ).collect(Collectors.toMap(MapEntry::getKey, MapEntry::getValue));
    }


    private static MapEntry<String, byte[]> file(final Path base, final Path file) {
        System.out.println("FILE: " + file);
        final String name = base.relativize(file).toString()
            .replace(File.separatorChar, '.')
            .replace("/", ".")
            .replace(".class", "");
        System.out.println("NAME: " + name);
        final UncheckedBytes bytes = new UncheckedBytes(new BytesOf(file.toAbsolutePath()));
        return new MapEntry<>(name, bytes.asBytes());
    }

}
