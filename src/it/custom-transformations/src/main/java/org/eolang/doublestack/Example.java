/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.doublestack;

import java.util.function.Consumer;

public class Example {
    public static void compute(boolean flag, Consumer<Double> consumer) {
        double value = flag ? 3.14 : 2.0;
        consumer.accept(value);
    }
}