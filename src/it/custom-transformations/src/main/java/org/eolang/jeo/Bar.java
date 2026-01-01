/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

public class Bar {
    public int foo(int x) {
        if (x > 0) {
            return 1;
        }
        return 2;
    }

    public void sipush() {
        short s = 256;
        System.out.println(s);
    }

    public int sum(int a, int b) {
        return a + b;
    }

    public int sub(int a, int b) {
        return a - b;
    }
}
