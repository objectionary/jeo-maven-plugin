/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.util.List;

public class Application {
    public static void main(String[] args) {
        new Developer<>(
            "Yegor",
            new Language() {
                public String name() {
                    return "EO";
                }
            }
        ).writeCode();
        new Developer<>(
            "Lev",
            new Language() {
                @Override
                public String name() {
                    return "Rust";
                }
            }
        ).writeCode();
        new Developer<>(
            "Maxim",
            new Language() {
                @Override
                public String name() {
                    return "PHP";
                }
            }
        ).writeCode();
        new Developer<>(
            "Roman",
            new Language() {
                @Override
                public String name() {
                    return "Kotlin";
                }
            }
        ).writeCode();
        GenericSelfCheck<Integer, List<Number>> self = new GenericSelfCheck<>();
        self.check();
    }
}
