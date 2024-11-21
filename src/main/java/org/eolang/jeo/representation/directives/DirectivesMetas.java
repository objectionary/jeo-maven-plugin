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
package org.eolang.jeo.representation.directives;

import com.jcabi.xml.XMLDocument;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.PrefixedName;
import org.xembly.Directive;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Directives for meta-information of a class.
 * @since 0.1
 */
public final class DirectivesMetas implements Iterable<Directive> {

    /**
     * Class name.
     */
    private final ClassName name;

    /**
     * Class directives.
     */
    private final DirectivesClass clazz;


    /**
     * Constructor.
     * @param classname Class name.
     */
    public DirectivesMetas(final ClassName classname) {
        this(classname, new DirectivesClass(new ClassName("SomeClass")));
    }

    /**
     * Constructor.
     * @param classname Class name.
     * @param clazz Class directives.
     */
    public DirectivesMetas(
        final ClassName classname,
        final DirectivesClass clazz
    ) {
        this.name = classname;
        this.clazz = clazz;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives metas = new Directives()
            .add("metas")
            .add("meta")
            .add("head").set("package").up()
            .add("tail").set(this.pckg()).up()
            .add("part").set(this.pckg()).up()
            .up();
        Set<String> objects = this.jeo();
        objects.stream()
            .filter(object -> !object.isEmpty())
            .map(DirectivesMetas::alias)
            .forEach(metas::append);
        return metas.up().iterator();
    }

    /**
     * Class name.
     * @return The class name.
     */
    ClassName className() {
        return this.name;
    }

    /**
     * Prefixed package name.
     * We intentionally add prefix to the packages, because sometimes they can be really
     * strange, <a href="https://github.com/objectionary/jeo-maven-plugin/issues/779">see</a>
     * @return Prefixed package name.
     */
    private String pckg() {
        final String result;
        final String original = this.name.pckg();
        if (original.isEmpty()) {
            result = "";
        } else {
            result = new PrefixedName(original).encode();
        }
        return result;
    }

    /**
     * Find all jeo objects.
     * @return Set of jeo objects.
     */
    private Set<String> jeo() {
        return new HashSet<>(
            new XMLDocument(
                new Xembler(
                    new Directives(this.clazz)
                ).xmlQuietly()
            ).xpath(".//o[starts-with(@base, 'jeo.')]/@base")
        );
    }

    /**
     * Alias directive for an object.
     * @param object Some object name.
     * @return Alias directive.
     */
    private static Directives alias(final String object) {
        return new Directives()
            .add("meta")
            .add("head").set("alias").up()
            .add("tail").set(object).up()
            .add("part").set(object).up()
            .up();
    }
}
