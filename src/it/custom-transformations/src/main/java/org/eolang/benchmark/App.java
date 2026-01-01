/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.benchmark;

class App {
    int run() {
        return new B(new A(42)).bar();
    }
}
