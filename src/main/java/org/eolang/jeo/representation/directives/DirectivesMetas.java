/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
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
 *
 * @since 0.1
 */
public final class DirectivesMetas implements Iterable<Directive> {

    /**
     * Absent package name.
     */
    private static final String ABSENT_PACKAGE = new AbsentPackage().toString();

    /**
     * Class name.
     */
    private final ClassName name;

    /**
     * Constructor.
     *
     * @param classname Class name
     */
    public DirectivesMetas(final ClassName classname) {
        this.name = classname;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives result = new Directives().add("metas");
        result.append(DirectivesMetas.home());
        final String pckg = this.name.pckg();
        if (pckg.isEmpty()) {
            result.append(DirectivesMetas.pckgd(DirectivesMetas.ABSENT_PACKAGE));
        } else {
            result.append(DirectivesMetas.pckgd(new PrefixedName(pckg).encode()));
        }
        result.append(DirectivesMetas.spdx());
        result.append(DirectivesMetas.version());
        return result.up().iterator();
    }

    private static Iterable<Directive> home() {
        return new Directives().add("meta")
            .add("head").set("home").up()
            .add("tail").set("https://github.com/objectionary/jeo-maven-plugin").up()
            .add("part").set("https://github.com/objectionary/jeo-maven-plugin").up()
            .up();
    }

    private static Directives pckgd(final String pckg) {
        return new Directives()
            .add("meta")
            .add("head").set("package").up()
            .add("tail").set(pckg).up()
            .add("part").set(pckg).up()
            .up();
    }

    private static Directives version() {
        return new Directives()
            .add("meta")
            .add("head").set("version").up()
            .add("tail").set(Manifests.read("JEO-Version")).up()
            .up();
    }

    private static Directives spdx() {
        final String spdx = new String(
            new char[]{
                'S', 'P', 'D', 'X',
                '-',
                'L', 'i', 'c', 'e', 'n', 's', 'e',
                '-', 'I', 'd', 'e', 'n', 't', 'i', 'f', 'i', 'e', 'r',
                ':',
            }
        );
        return new Directives()
            .add("meta")
            .add("head").set("spdx").up()
            .add("tail").set(String.format("%s MIT", spdx)).up()
            .add("part").set(spdx).up()
            .add("part").set("MIT").up()
            .up();
    }
}
