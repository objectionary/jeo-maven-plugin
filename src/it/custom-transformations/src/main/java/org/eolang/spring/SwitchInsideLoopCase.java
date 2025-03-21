/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.spring;

public class SwitchInsideLoopCase {
    public int example(byte x) {
        for (int i = 0; i < 10; ++i) {
            switch (x) {
                case 1:
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected value: " + x);
            }
        }
        return 2;
    }

}
