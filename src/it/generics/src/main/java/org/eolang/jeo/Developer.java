/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.lang.String;
import java.lang.System;

public class Developer<T extends Language> {

    private final String name;
    private final T lang;

    public Developer(final String name, final T lang) {
        this.name = name;
        this.lang = lang;
    }

    void writeCode() {
        System.out.println(
            String.format("%s likes write code in %s", this.name, lang.name())
        );
    }
}
