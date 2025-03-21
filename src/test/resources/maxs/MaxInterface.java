/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
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
