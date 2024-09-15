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
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;

/**
 * Xmir representation of a method parameter.
 * @since 0.4
 * @todo #596:30min Add unit tests for the XmlParam class.
 *  We should add unit tests for the XmlParam class. The tests should cover
 *  the following methods: {@link #index()} and {@link #annotations()}.
 *  This will help us to ensure that the class is working as expected.
 */
public final class XmlParam {

    /**
     * Index of the parameter in the method.
     */
    private final int position;

    /**
     * Root node from which we will get all required data.
     */
    private final XmlNode root;

    /**
     * Constructor.
     * @param position Index of the parameter in the method.
     * @param root Root node.
     */
    public XmlParam(final int position, final XmlNode root) {
        this.position = position;
        this.root = root;
    }

    /**
     * Index of the parameter in the method.
     * @return Index.
     */
    public int index() {
        return this.position;
    }

    /**
     * Annotations of the parameter.
     * @return Annotations.
     */
    public List<BytecodeAnnotation> annotations() {
        return this.root.children()
            .filter(node -> node.hasAttribute("base", "annotation"))
            .map(XmlAnnotation::new)
            .map(XmlAnnotation::bytecode)
            .collect(Collectors.toList());
    }

}
