/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
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
        result.append(DirectivesMetas.spdx());
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
     * SPDX directives.
     * @return SPDX directives.
     */
    private static Directives spdx() {
        return new Directives()
            .add("meta")
            .add("head").set("spdx").up()
            .add("tail").set("SPDX-License-Identifier: MIT").up()
            .add("part").set("SPDX-License-Identifier:").up()
            .add("part").set("MIT").up()
            .up();
    }
}
