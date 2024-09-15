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
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;

/**
 * Xmir annotations.
 * @since 0.1
 * @todo #292:90min Add unit test for XmlAnnotations class.
 *  XmlAnnotations class is not covered by unit tests.
 *  Add unit tests for XmlAnnotations class.
 *  Don't forget to remove the current puzzle.
 */
public class XmlAnnotations {

    /**
     * XML node representing annotations.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param xmlnode XML node.
     */
    XmlAnnotations(final XmlNode xmlnode) {
        this.node = xmlnode;
    }

    /**
     * Convert to bytecode.
     * @return Bytecode annotations.
     */
    public BytecodeAnnotations bytecode() {
        return new BytecodeAnnotations(this.all().stream().map(XmlAnnotation::bytecode));
    }

    /**
     * All annotations.
     * @return Annotations.
     */
    private List<XmlAnnotation> all() {
        return this.node.children()
            .map(XmlAnnotation::new)
            .collect(Collectors.toList());
    }
}
