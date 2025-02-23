/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
/**
 * Package for generating XMIR by using ASM library.
 * This package contains different visitors that are used to build XML directives.
 * In other words all classes in this package scan bytecode by using ASM library
 * (<a href="https://asm.ow2.io/">https://asm.ow2.io/</a>) and build XML directives
 * by using Xembly library (<a href="https://www.xembly.org">https://www.xembly.org</a>).
 * Later on these directives are used to build XML representation of the bytecode class.
 *
 * @since 0.1.0
 */
package org.eolang.jeo.representation.directives;
