/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.innerclasses;

public class Top {

    public void print() {
        new TopInnerStatic().print();
        this.new TopInnerInstance().print();
    }

    private static class TopInnerStatic {
        public void print() {
            System.out.println("TopInnerStatic");
        }
    }

    private class TopInnerInstance {
        public void print() {
            System.out.println("TopInnerInstance");
        }
    }
}
