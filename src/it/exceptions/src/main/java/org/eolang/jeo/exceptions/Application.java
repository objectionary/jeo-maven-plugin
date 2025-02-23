/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
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
