/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Greeter bean.
 * @since 0.2
 */
@RestController
public class Receptionist {

    @GetMapping("/hello")
    public String greetings(
        @RequestParam(defaultValue = "Fat Spring") final String who
    ) {
        return String.format("Glad to see you, %s...", who);
    }
}
