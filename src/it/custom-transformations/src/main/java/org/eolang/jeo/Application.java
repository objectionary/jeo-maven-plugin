/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import org.eolang.jeo.Message;
import org.eolang.jeo.HelloWorld;
import org.eolang.jeo.WakeUpNeo;
import java.util.function.Predicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Application {
    public static void main(String[] args) {
        dynamicDispatch();
        functionalInterfaces();
        streams();
    }

    private static void dynamicDispatch() {
        Message message;
        if (bar(1.0d) < 7) {
            message = new HelloWorld();
        } else {
            message = new WakeUpNeo();
        }
        long a = 0L;
        a = a + 123L;
        System.out.println(message.msg());
    }

    private static int bar(double x) {
        if (x > 0.0d) {
            return 5;
        }
        return 8;
    }

    private static void functionalInterfaces() {
        Runnable r = () -> System.out.println("Runnable test passed successfully!");
        r.run();
        Predicate<String> p = s -> s.length() > 10;
        System.out.println(
            "Predicate test passed successfully " +
                p.test("String length is greater than 10!") +
                "!"
        );
        Consumer<String> c = s -> System.out.println("Consumer test passed successfully with " + s);
        c.accept("Consumer!");
        Function<String, Integer> f = s -> s.length();
        System.out.println("Function test passed successfully with " + f.apply("Function") + "!");
    }

    private static void streams() {
        List<String> list = Arrays.asList("Streams", "Test", "paSSed", "successfuLLy!");
        final String res = list.stream().map(String::toLowerCase).collect(Collectors.joining(" "));
        System.out.println(res);
    }
}
