/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.benchmark;

class A implements F {
    private int d;

    A(int d) {
        this.d = d;
    }

    @Override
    public int foo() {
        return d + 1;
    }
}
