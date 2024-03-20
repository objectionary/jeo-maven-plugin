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

import java.util.List;
import java.util.stream.Collectors;
import org.objectweb.asm.Handle;

/**
 * XML representation of handler.
 * @since 0.3
 */
final class XmlHandler {

    /**
     * XML node.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param node Node.
     */
    public XmlHandler(final XmlNode node) {
        this.node = node;
    }

    /**
     * Convert to a handler.
     * @return Handler.
     */
    public Handle asHandle() {
        final List<XmlOperand> operands = this.node.children()
            .map(XmlOperand::new)
            .collect(Collectors.toList());
        return new Handle(
            Integer.class.cast(operands.get(0).asObject()),
            operands.get(1).asObject().toString(),
            operands.get(2).asObject().toString(),
            operands.get(3).asObject().toString(),
            Boolean.class.cast(operands.get(4).asObject())
        );
    }
}
