/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.spring;

import org.springframework.stereotype.Component;

@Component
public class Receptionist {
    public void sayHello(final String who) {
        System.out.printf("Glad to see you, %s...%n", who);
    }
}
