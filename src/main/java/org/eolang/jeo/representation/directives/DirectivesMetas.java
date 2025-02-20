/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
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

import com.jcabi.manifests.Manifests;
import java.util.Iterator;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.PrefixedName;
import org.xembly.Directive;
import org.xembly.Directives;

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
     * Constructor.
     * @param classname Class name.
     */
    public DirectivesMetas(final ClassName classname) {
        this.name = classname;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives result = new Directives().add("metas");
        result.append(DirectivesMetas.home());
        if (!this.name.pckg().isEmpty()) {
            result.append(this.pckg());
        }
        result.append(DirectivesMetas.unlint());
        result.append(DirectivesMetas.version());
        return result.up().iterator();
    }

    /**
     * Class name.
     * @return The class name.
     */
    ClassName className() {
        return this.name;
    }

    /**
     * Home directives.
     * @return Directives for home.
     */
    private static Iterable<Directive> home() {
        return new Directives().add("meta")
            .add("head").set("home").up()
            .add("tail").set("https://github.com/objectionary/jeo-maven-plugin").up()
            .add("part").set("https://github.com/objectionary/jeo-maven-plugin").up()
            .up();
    }

    /**
     * Prefixed package.
     * We intentionally add prefix to the packages, because sometimes they can be really
     * strange, <a href="https://github.com/objectionary/jeo-maven-plugin/issues/779">see</a>
     * @return Prefixed package name.
     */
    private Directives pckg() {
        final String prefixed = new PrefixedName(this.name.pckg()).encode();
        return new Directives()
            .add("meta")
            .add("head").set("package").up()
            .add("tail").set(prefixed).up()
            .add("part").set(prefixed).up()
            .up();
    }

    /**
     * The version directives of jeo-maven-plugin.
     * @return Version directives.
     */
    private static Directives version() {
        return new Directives()
            .add("meta")
            .add("head").set("version").up()
            .add("tail").set(Manifests.read("JEO-Version")).up()
            .up();
    }

    /**
     * Ignored XMIR checks.
     * @return Directives for ignored checks.
     */
    private static Directives unlint() {
        return new Directives()
            .add("meta")
            .add("head").set("unlint").up()
            .add("tail").set("mandatory-package").up()
            .up();
    }
}
