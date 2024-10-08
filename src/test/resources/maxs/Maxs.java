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

import java.lang.Math;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * This class contains many different methods with different number of local
 * variables and stack elements.
 * Primarly this class is used in {@link BytecodeMethodTest} to test how the
 * {@link BytecodeMethod} class counts the maxs.
 * @since 0.6
 */
public class Maxs {

    public static double DOUBLE_CONSTANT = 3.14d;
    public static float FLOAT_CONSTANT = 3.14f;
    private int someIntField = 42;
    private long someLongField = 42L;
    private Collection<Object> collection;
    private int index;
    private boolean growCollection;
    private int maximumSize;

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
     * This method has 5 local variables ('double' types and 'this') and 2 stack elements.
     * @param a
     * @param b
     * @return a / b
     */
    public double div(double a, double b) {
        return a / b;
    }

    /**
     * This method has 4 local ('long' types) variables and 2 stack elements.
     * @param a
     * @param b
     * @return a * b
     */
    public static long mul(long a, long b) {
        return a * b;
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

    /**
     * This method has 25 local variables because it uses 'long' types.
     */
    public long manyLocals2() {
        long a = System.currentTimeMillis();
        long b = System.currentTimeMillis();
        long c = System.currentTimeMillis();
        long d = System.currentTimeMillis();
        long e = System.currentTimeMillis();
        long f = System.currentTimeMillis();
        long g = System.currentTimeMillis();
        long h = System.currentTimeMillis();
        long i = System.currentTimeMillis();
        long j = System.currentTimeMillis();
        long k = System.currentTimeMillis();
        long l = System.currentTimeMillis();
        return a + b + c + d + e + f + g + h + i + j + k + l;
    }

    /**
     * This method has 6 local variables (including 'this') and 3 stack elements.
     * It includes a loop with a local variable declared inside.
     * @param limit
     * @return the sum from 0 to limit
     */
    public int sumWithLoop(int limit) {
        int sum = 0;       // local variable 1
        int i = 0;         // local variable 2
        for (; i < limit; i++) { // 'i' is reused
            int temp = i;   // local variable 3 (reused in each loop iteration)
            sum += temp;
        }
        return sum;
    }

    /**
     * This method has 4 local variables (including 'this') and 4 stack elements.
     * It includes conditional statements with local variables declared inside branches.
     * @param flag
     * @return different values based on flag
     */
    public int conditionalMethod(boolean flag) {
        if (flag) {
            int a = 10;    // local variable 1
            return a;
        } else {
            int b = 20;    // local variable 2
            return b;
        }
    }

    /**
     * This method has 5 local variables (including 'this') and 5 stack elements.
     * It includes a try-catch block with a local variable in the catch clause.
     * @param x
     * @param y
     * @return division result or -1 if exception occurs
     */
    public int tryCatchMethod(int x, int y) {
        try {
            int result = x / y; // local variable 1
            return result;
        } catch (ArithmeticException e) { // local variable 2
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * This method has 7 local variables (including 'this') and 4 stack elements.
     * It includes multiple return points and a finally block.
     * @param a
     * @param b
     * @return the greater of a or b
     */
    public int multipleReturns(int a, int b) {
        try {
            if (a > b) {
                return a; // return point 1
            } else {
                return b; // return point 2
            }
        } finally {
            System.out.println("Finally block executed");
        }
    }

    /**
     * This method has 5 local variables (including 'this') and 3 stack elements.
     * It uses a switch-case statement with local variables inside cases.
     * @param option
     * @return based on option
     */
    public String switchCaseMethod(int option) {
        String result;
        switch (option) {
            case 1:
                result = "One"; // local variable 1
                break;
            case 2:
                result = "Two"; // local variable 2
                break;
            default:
                result = "Other"; // local variable 3
                break;
        }
        return result;
    }

    /**
     * This method has 4 local variables (including 'this') and 2 stack elements.
     * It uses recursion.
     * @param n
     * @return factorial of n
     */
    public int recursiveFactorial(int n) {
        if (n <= 1) {
            return 1;
        }
        return n * recursiveFactorial(n - 1);
    }

    /**
     * This method has 8 local variables (including 'this') and 3 stack elements.
     * It uses nested loops with multiple local variables.
     * @return the product of sums
     */
    public int nestedLoops() {
        int product = 1; // local variable 1
        for (int i = 0; i < 5; i++) { // local variable 2
            int sum = 0; // local variable 3
            for (int j = 0; j < 5; j++) { // local variable 4
                sum += j;
            }
            product *= sum;
        }
        return product;
    }

    /**
     * This method has 9 local variables (including 'this') and 4 stack elements.
     * It includes a loop with try-catch inside.
     * @param numbers
     * @return sum of numbers
     */
    public int loopWithTryCatch(int[] numbers) {
        int sum = 0; // local variable 1
        for (int i = 0; i < numbers.length; i++) { // local variable 2
            try {
                sum += numbers[i]; // local variable 3
            } catch (ArrayIndexOutOfBoundsException e) { // local variable 4
                e.printStackTrace();
            }
        }
        return sum;
    }

    /**
     * This method has 6 local variables (including 'this') and 3 stack elements.
     * It uses an array and manipulates its elements.
     * @param size
     * @return the sum of array elements
     */
    public int arrayManipulation(int size) {
        int[] array = new int[size]; // local variable 1
        int sum = 0; // local variable 2
        for (int i = 0; i < size; i++) { // local variable 3
            array[i] = i * 2;
            sum += array[i];
        }
        return sum;
    }

    /**
     * This method has 7 local variables (including 'this') and 4 stack elements.
     * It creates and uses an inner class instance.
     * @return concatenated string
     */
    public String innerClassMethod() {
        StringBuilder sb = new StringBuilder(); // local variable 1
        Inner inner = new Inner(); // local variable 2
        String part1 = inner.partOne(); // local variable 3
        String part2 = inner.partTwo(); // local variable 4
        sb.append(part1).append(part2);
        return sb.toString();
    }

    /**
     * Loads double constant.
     * This method has 3 local variables (including 'this') and 4 stack elements.
     */
    public double loadDoubleConstant() {
        double d = Math.PI;
        return d * 3d;
    }

    /**
     * Loads float constant.
     * This method has 2 local variables (including 'this') and 2 stack elements.
     */
    public float loadFloatConstant() {
        float f = Maxs.FLOAT_CONSTANT;
        return f * 3f;
    }

    /**
     * Put double constant.
     * This method has 1 local variables (including 'this') and 1 stack elements.
     */
    public long putDoubleConstant() {
        Maxs.DOUBLE_CONSTANT = 42L;
        return 3L;
    }

    /**
     * Put float constant.
     */
    public int putFloatConstant() {
        Maxs.FLOAT_CONSTANT = 42f;
        return 3;
    }

    /**
     * Loads int field
     */
    public int loadIntField() {
        return new Maxs().someIntField;
    }

    /**
     * Loads long field
     */
    public long loadLongField() {
        return new Maxs().someLongField;
    }

    /**
     * Puts long field
     * @param value Value to put
     */
    public void putsLongField(final long value) {
        this.someLongField = value;
    }

    /**
     * Puts int field
     * @param value Value to put
     */
    public void putsIntField(final int value) {
        this.someIntField = value;
    }

    /**
     * Multiarray example.
     */
    public int[][][] multiarrayExample() {
        int[][][] multiarray = new int[2][3][4];
        long[][][][] longMultiarray = new long[2][3][4][5];
        longMultiarray[1][2][3][4] = 42L;
        longMultiarray[1][1][3][4] = 42L;
        multiarray[1][2][3] = (int) longMultiarray[1][2][3][4] + (int) longMultiarray[1][1][3][4];
        return multiarray;
    }

    /**
     * Lookup switch example.
     */
    public int tableSwitchExample(int key) {
        switch (key) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            default:
                return -1;
        }
    }

    /**
     * Table switch example.
     */
    public int lookupSwitchExample(int key) {
        switch (key) {
            case 1:
                return 1;
            case 10:
                return 2;
            case 100:
                return 3;
            case 1000:
                return 4;
            default:
                return -1;
        }
    }

    private long invokeSpecial() {
        return new Maxs().privateMethod(3, 4) * 2L;
    }

    private long privateMethod(long a, long b) {
        System.out.println("Private method");
        return a + b;
    }

    private static int invokeStatic() {
        return staticMethod(10, 5) * 2;
    }

    private static int staticMethod(int a, int b) {
        System.out.println("Static method");
        return a - b;
    }

    private void ifstatement(int initial) {
        if (initial > 10) {
            long a = 42L * System.currentTimeMillis();
            long b = 42L * System.currentTimeMillis();
            int d = 10;
            int x1 = 10;
            int x2 = 20;
            int x3 = 30;
            int x4 = 40;
            int x5 = 50;
            int x6 = 60;
            int x7 = 70;
            a = a + b + x1 + x2 + x3 + x4 + x5 + x6 + x7;
            a += d;
            System.out.println("Hello: " + a + b);
        } else {
            long a = 42L * System.currentTimeMillis();
            int b = 10;
            a = a + b;
            int c = 112;
            a += c;
            b += c;
            a += b;
            System.out.println("World: " + a);
        }
    }

    public int sumUpTo(int n) {
        int sum = 0;
        for (int i = 1; i <= n; i++) {
            sum += i;
        }
        return sum;
    }

    public String categorizeNumber(int x) {
        if (x > 0) {
            if (x % 2 == 0) {
                return "Positive Even";
            } else {
                return "Positive Odd";
            }
        } else {
            return "Non-Positive";
        }
    }

    public int divide(int a, int b) {
        try {
            return a / b;
        } catch (ArithmeticException e) {
            System.out.println("Division by zero!");
            return 0;
        }
    }


    public int factorial(int n) {
        if (n <= 1) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }

    public boolean isEvenAndPositive(int x) {
        return (x > 0) && (x % 2 == 0);
    }

    public boolean isEitherZeroOrNegative(int x) {
        return (x == 0) || (x < 0);
    }

    public int countDown(int start) {
        int count = start;
        while (count > 0) {
            count--;
        }
        return count;
    }

    public int findFirstPositive(int[] numbers) {
        int index = 0;
        int result = -1;
        do {
            if (numbers[index] > 0) {
                result = numbers[index];
                break;
            }
            index++;
        } while (index < numbers.length);
        return result;
    }

    public int sumElements(List<Integer> list) {
        int sum = 0;
        for (int num : list) {
            sum += num;
        }
        return sum;
    }

    public int multiplyMatrices(int[][] matrixA, int[][] matrixB) {
        int rows = matrixA.length;
        int cols = matrixB[0].length;
        int[][] result = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < matrixA[0].length; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
        return result[0][0];
    }

    public void readFile(String filename) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void processFile(String filename) {
        try {
            int number = Integer.parseInt(filename);
            System.out.println("Number: " + number);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        } catch (Exception e) {
            System.out.println("An error occurred.");
        }
    }

    public void writeFile(String filename, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void infiniteLoopWithBreak() {
        int i = 0;
        while (true) {
            if (i > 10) {
                break;
            }
            i++;
        }
    }

    public int sumOddNumbers(int[] numbers) {
        int sum = 0;
        for (int num : numbers) {
            if (num % 2 == 0) {
                continue;
            }
            sum += num;
        }
        return sum;
    }

    public void validateAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative.");
        }
        System.out.println("Age is valid.");
    }

    public Function<String, Integer> stringLengthMethodRefStreamed() {
        return String::length;
    }

    public long countEvenNumbersStreamed(List<Integer> numbers) {
        return numbers.stream().filter(n -> n % 2 == 0).count();
    }

    public boolean isEvenAndPositiveStreamed(int x) {
        return Stream.of(x).allMatch(n -> n > 0 && n % 2 == 0);
    }

    public static int problematicStatic(String[] args) {
        String obj = args[0];
        return obj.length();
    }

    public int switchInsideLoopCase(byte x) {
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

    public Object getValue() {
        growCollectionIfNecessary();
        if (collection instanceof List) {
            return ((List<Object>) collection).get(index);
        } else {
            int pos = 0;
            for (Object o : collection) {
                if (pos == index) {
                    return o;
                }
                pos++;
            }
            throw new IllegalStateException(
                "Failed to find indexed element " + index + ": " + collection);
        }
    }

    public void setValue(Object newValue) {
        growCollectionIfNecessary();
        if (collection instanceof List) {
            List<Object> list = (List<Object>) collection;
            list.set(index, newValue);
        } else {
            throw new UnsupportedOperationException(
                "Indexing not supported for type: " + collection.getClass().getName());
        }
    }

    private void growCollectionIfNecessary() {
        if (this.index >= this.collection.size()) {
            if (this.growCollection) {
                throw new IllegalStateException(
                    "Collection index out of bounds: size=" + collection.size() + ", index=" + index + ", grow=" + new Object[]{this.collection.size(), this.index});
            }
            try {
                Constructor<?> ctor = this.getDefaultConstructor(this.getClass());
                for (int newElements = this.index - this.collection.size(); newElements >= 0; --newElements) {
                    this.collection.add(ctor != null ? ctor.newInstance() : null);
                }
            } catch (Throwable e) {
                throw new IllegalStateException("Something else: " + e.getMessage());
            }
        }
    }

    private Constructor<?> getDefaultConstructor(Class<?> type) {
        try {
            return type.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public boolean isWritable() {
        return true;
    }


    // Inner class to add complexity
    private class Inner {
        public String partOne() {
            return "Hello, ";
        }

        public String partTwo() {
            return "World!";
        }
    }
}
