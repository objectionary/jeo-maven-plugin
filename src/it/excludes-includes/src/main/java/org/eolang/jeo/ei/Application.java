/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.ei;

import org.eolang.jeo.ei.included.Included;
import org.eolang.jeo.ei.included.NotIncluded;
import org.eolang.jeo.ei.ignored.Ignored;
import org.eolang.jeo.ei.ignored.deep.DeeplyIgnored;

/**
 * Application Entry Point That Uses Different Exception Handlers.
 * @since 0.1
 */
public class Application {
    public static void main(String[] args) throws Exception {
        new Included().say();
        new NotIncluded().say();
        new Ignored().say();
        new DeeplyIgnored().say();
    }
}
