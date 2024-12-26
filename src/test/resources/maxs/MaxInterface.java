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
public interface MaxInterface {

    /**
     * Method with one local variable (this) and no stack elements.
     * Attention! Abstract methods don't have local variables and stack elements.
     */
    void noVariablesNoStack();

    /**
     * Method without local variables and with one stack element.
     * Attention! Abstract methods don't have local variables and stack elements.
     */
    void oneVariableNoStack(int var);

    /**
     * Method with one local variable (this) and two stack elements.
     */
    default void noVariablesNoStackDefault() {
        System.out.println("Hello, world!");
    }

}
