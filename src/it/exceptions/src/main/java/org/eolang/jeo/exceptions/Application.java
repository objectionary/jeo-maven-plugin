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
package org.eolang.jeo.exceptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;

/**
 * Application Entry Point That Uses Different Exception Handlers.
 * @since 0.1
 */
public class Application {

    public static void main(String[] args) throws Exception {
        Application.simpleTryCatch();
        Application.tryCatchWithFinally();
        Application.tryWithResources();
        Application.tryCatchWithResources();
        Application.suppressedException();
        Application.methodThatDeclaresException();
    }

    /**
     * Simple try-catch statement.
     */
    private static void simpleTryCatch() {
        try {
            throw new IOException("Exception in try-catch statement");
        } catch (final IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Try-catch-finally statement.
     */
    private static void tryCatchWithFinally() {
        try {
            throw new IOException("Exception in try-catch-finally statement");
        } catch (final IOException exception) {
            System.out.println(exception.getMessage());
        } finally {
            System.out.println("Finally block in try-catch-finally statement");
        }
    }

    /**
     * Try-with-resources statement.
     */
    private static void tryWithResources() {
        try (MyResource resource = new MyResource("Resource Without Exception")) {
//            Do nothing.
        }
    }

    /**
     * Try-catch-with-resources statement.
     */
    private static void tryCatchWithResources() {
        try (MyResource resource = new MyResource("Resource With Exception")) {
            throw new IllegalStateException("Exception in try-catch-with-resources statement");
        } catch (final IllegalStateException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Try-catch-with-resources statement with suppressed exception.
     */
    private static void suppressedException() {
        try (ResourceWithException resource = new ResourceWithException()) {
            throw new IllegalStateException(
                "Exception in try-catch-with-resources statement with suppressed exception");
        } catch (final IllegalStateException exception) {
            System.out.println(exception.getMessage());
            for (final Throwable suppressed : exception.getSuppressed()) {
                System.out.println(suppressed.getMessage());
            }
        }
    }

    /**
     * Method that declares exception.
     * @throws Exception Exception.
     */
    private static void methodThatDeclaresException() throws Exception {
        new File("").getCanonicalFile();
    }
}
