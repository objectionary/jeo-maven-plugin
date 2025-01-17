/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
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

import java.util.Objects;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.bytecode.BytecodeMaxs;

/**
 * Xmir representation of max stack and max locals of a method.
 * @since 0.3
 */
@EqualsAndHashCode
@ToString
public final class XmlMaxs {

    /**
     * XML node.
     */
    @EqualsAndHashCode.Exclude
    private final XmlNode node;

    /**
     * Constructor.
     *
     * @param node XML node.
     */
    XmlMaxs(final XmlNode node) {
        this.node = node;
    }

    /**
     * Convert to bytecode maxs.
     * @return Bytecode maxs.
     */
    public BytecodeMaxs bytecode() {
        return new BytecodeMaxs(this.stack(), this.locals());
    }

    /**
     * Stack max size.
     * @return Stack size.
     */
    @EqualsAndHashCode.Include
    private int stack() {
        return this.ichild(0);
    }

    /**
     * Locals max size.
     * @return Locals size.
     */
    @EqualsAndHashCode.Include
    private int locals() {
        return this.ichild(1);
    }

    /**
     * Retrieve integer child.
     * @param position Position.
     * @return Integer value.
     */
    private int ichild(final int position) {
        return (int) Objects.requireNonNull(
            new XmlOperand(
                this.node.children().collect(Collectors.toList()).get(position)
            ).asObject(),
            String.format(
                "The XML node representing Maxs '%s' doesn't contain a valid integer at '%d' position",
                this.node,
                position
            )
        );
    }
}
