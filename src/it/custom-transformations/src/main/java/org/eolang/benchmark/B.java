/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.benchmark;

class B {
    private final F f;

    B(F f) {
        this.f = f;
    }

    int bar() {
        return f.foo() + 2;
    }
}
