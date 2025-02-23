/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

/**
 * XML document abstraction.
 * We added it to be able to use two different implementations of XML documents:
 * - one from the jcabi library
 * - another native implementation
 * @since 0.7
 */
public interface XmlDoc {

    /**
     * Root node.
     * @return Root node.
     */
    XmlNode root();

    void validate();
}
