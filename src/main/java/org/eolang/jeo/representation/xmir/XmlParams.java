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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameters;

/**
 * XML method params.
 * @since 0.6
 */
public final class XmlParams {

    /**
     * Xml representation of a method params.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param node Xml representation of a method params.
     */
    public XmlParams(final XmlNode node) {
        this.node = node;
    }

    /**
     * Get method params.
     * @return Method params.
     */
    public BytecodeMethodParameters params() {
        final AtomicInteger index = new AtomicInteger(0);
        return new BytecodeMethodParameters(
            this.node.children()
                .filter(
                    element -> element.hasAttribute("base", "param")
//                        && !element.hasAttribute("name", "maxs")
                )
                .map(element -> new XmlParam(index.getAndIncrement(), element))
                .map(XmlParam::bytecode)
                .collect(Collectors.toList())
        );
    }
}
