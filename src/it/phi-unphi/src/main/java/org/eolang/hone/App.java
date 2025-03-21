/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.hone;

import org.eolang.hone.param.Parameter;

/**
 * App.
 * @since 0.1
 */
@Parameter("some-parameter")
public class App {
    private static final String Φ = "We have the field with the unicode character 'Φ'";
    @Deprecated
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        double angle = 42.0;
        double sin = Math.sin(angle);
        System.out.printf("sin(%f) = %f\n", angle, sin);
        System.out.println(Φ);
    }
}
