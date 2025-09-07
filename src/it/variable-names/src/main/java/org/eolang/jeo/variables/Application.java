/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.variables;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;
import java.util.HashSet;
import org.eolang.jeo.lines.Lines;

/**
 *  Application Entry Point.
 * @since 0.1
 */
public class Application {
    public static void main(String[] args) throws Exception {
        Set<String> names = new HashSet(0);
        for (Method someMethod : Application.class.getMethods()) {
            if (someMethod.getName().equals("someMethod")) {
                for (Parameter parameter : someMethod.getParameters()) {
                    names.add(parameter.getName());
                }
            }
        }
        if (names.size() != 2) {
            throw new Exception(
                "Expected 2 parameters, but got " + names.size() + " instead. All found methods: " + names);
        }
        if (!names.contains("firstParam")) {
            throw new Exception(
                "Expected parameter 'firstParam' not found. All found methods: " + names);
        }
        if (!names.contains("secondParam")) {
            throw new Exception(
                "Expected parameter 'secondParam' not found. All found methods: " + names);
        }
        someMethod("firstParam", "secondParam");
        Lines.checkTransformationPreserveLines();
    }

    public static void someMethod(final String firstParam, final String secondParam) {
        System.out.println("First param: " + firstParam);
        System.out.println("Second param: " + secondParam);
    }

}
