/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

public class HelloWorld implements Message {

    @Override
    public String msg() {
        return "Hello, World!";
    }
}
