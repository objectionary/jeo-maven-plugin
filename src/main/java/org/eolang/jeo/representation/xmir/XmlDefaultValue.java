/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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

import java.util.Optional;
import org.eolang.jeo.representation.bytecode.BytecodeDefaultValue;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;

/**
 * XMIR of annotation default value.
 *
 * @since 0.3
 */
public final class XmlDefaultValue {

    /**
     * Default value XMIR node.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param node Default value XMIR node.
     */
    XmlDefaultValue(final XmlNode node) {
        this.node = node;
    }

    /**
     * Convert to bytecode.
     * @return Bytecode default value.
     */
    public Optional<BytecodeDefaultValue> bytecode() {
        return this.node.children().findFirst().map(
            property -> new BytecodeDefaultValue(
                new XmlAnnotationProperty(property).bytecode()
            )
        );
    }

    /**
     * Write to method.
     * @param method Method.
     */
    public void writeTo(final BytecodeMethod method) {
        this.bytecode().ifPresent(method::defvalue);
    }

}
