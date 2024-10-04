/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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

/**
 * This class contains many different methods with different number of local
 * variables and stack elements.
 * Primarly this class is used in {@link BytecodeMethodTest} to test how the
 * {@link BytecodeMethod} class counts the maxs.
 * @since 0.6
 */
public class Maxs {

    public static void main(String[] args) {
        System.out.println(fortyTwo());
        System.out.println("passed");
    }

    /**
     * This method has 0 local variables and 1 stack element.
     * @return 42
     */
    static byte fortyTwo() {
        return 42;
    }

    /**
     * This method has 3 local variables (including 'this') and 2 stack elements.
     * @param a
     * @param b
     * @return a + b
     */
    public int add(int a, int b) {
        return a + b;
    }

    /**
     * This method has 2 local variables and 2 stack elements.
     * @param a
     * @param b
     */
    public static int sub(int a, int b) {
        return a - b;
    }

    /**
     * This method has 11 (including 'this') local variables and 1 stack element.
     */
    public int manyLocals() {
        int a = (int) System.currentTimeMillis();
        int b = (int) System.currentTimeMillis();
        int c = (int) System.currentTimeMillis();
        int d = (int) System.currentTimeMillis();
        int e = (int) System.currentTimeMillis();
        int f = (int) System.currentTimeMillis();
        int g = (int) System.currentTimeMillis();
        int h = (int) System.currentTimeMillis();
        int i = (int) System.currentTimeMillis();
        int j = (int) System.currentTimeMillis();
        return a + b + c + d + e + f + g + h + i + j;
    }
}