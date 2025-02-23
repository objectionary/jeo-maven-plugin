/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.hone.mess;

/**
 * This class was added to add more classes for phi/unphi tests.
 * @since 0.6
 */
public class A {
    public static void main(String[] args) {
        System.out.println("Hello from A!");
    }

    public static void print() {
        System.out.println("Hello from A!");
    }

    public static void print(String message) {
        System.out.println(message);
    }

    public static void print(String message, String message2) {
        System.out.println(message + " " + message2);
    }
}
