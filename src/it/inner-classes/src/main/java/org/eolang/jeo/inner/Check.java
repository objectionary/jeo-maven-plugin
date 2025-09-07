/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.inner;

import java.util.Objects;

public class Check {

    // Private members that nestmates will access directly
    private static String SECRET = "shh";
    private int x = 41;

    /**
     * Static nested class: touches private static members in a static initializer.
     * If NestHost/NestMembers are absent, initializing this class will throw IllegalAccessError.
     */
    public static class StaticNestMate {
        static {
            // Direct access to private static members of Check
            SECRET = SECRET + "!";
        }

        public String name() {
            return "StaticNestMate(" + SECRET + ")";
        }
    }

    /**
     * Non-static inner class: touches both private instance and private static members.
     */
    public class Inner {
        public Inner() {
            // Direct access to outer's private instance field and private static field
            x += SECRET.length();
        }

        public String name() {
            return "Inner(x=" + x + ")";
        }
    }

    // Private method used only by nestmates
    private void bump() {
        x++;
    }

    public static void main(String[] args) {
        // 1) Triggers StaticNestMate.<clinit> which accesses private static members
        StaticNestMate s = new StaticNestMate();

        // 2) Inner constructor touches private instance/static members
        Check outer = new Check();
        Inner in = outer.new Inner();

        // 3) Local class that directly touches private members
        class Local {
            String name() {
                // Touch private instance and private static members
                outer.bump();
                return "Local(" + outer.x + "," + SECRET + ")";
            }
        }
        Local local = new Local();

        // 4) Anonymous class that also touches private members
        Runnable anon = new Runnable() {
            @Override public void run() {
                outer.bump(); // private instance method
                // Also reference SECRET to force private static access
                if (SECRET.isEmpty()) {
                    throw new AssertionError("unreachable");
                }
            }
        };
        anon.run();

        // Keep references live; avoid DCE
        int hash = Objects.hash(s.name(), in.name(), local.name(), anon.getClass().getName());
        if (hash == Integer.MIN_VALUE) System.out.println("Impossible branch");

        System.out.println("Inner classes work well!");
    }
}
