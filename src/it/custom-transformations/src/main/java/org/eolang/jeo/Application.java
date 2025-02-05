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
        funcionalInterfaces();
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

    private static void funcionalInterfaces() {
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
