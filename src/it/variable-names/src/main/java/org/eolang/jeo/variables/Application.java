/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo.variables;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;
import java.util.HashSet;

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
    }

    public static void someMethod(final String firstParam, final String secondParam) {
        System.out.println("First param: " + firstParam);
        System.out.println("Second param: " + secondParam);
    }

}
