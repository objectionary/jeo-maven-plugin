/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.spring;

/**
 * App.
 * @since 0.1
 */
public class App {
    public static void main(String[] args) {
        double angle = 42.0;
        double sin = Math.sin(angle);
        System.out.printf("sin(%f) = %f\n", angle, sin);
    }
}
