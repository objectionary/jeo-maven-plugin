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

import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesMaxs;
import org.xembly.Xembler;

/**
 * Xmir representation of max stack and max locals of a method.
 *
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
     * @param stack Stack size.
     * @param locals Locals size.
     */
    public XmlMaxs(final int stack, final int locals) {
        this(XmlMaxs.prestructor(stack, locals));
    }

    /**
     * Constructor.
     *
     * @param node XML node.
     */
    public XmlMaxs(final XmlNode node) {
        this.node = node;
    }

    /**
     * Stack max size.
     *
     * @return Stack size.
     */
    @EqualsAndHashCode.Include
    public int stack() {
        return (int) new XmlOperand(
            this.node.children().collect(Collectors.toList()).get(0)
        ).asObject();
    }

    /**
     * Locals max size.
     *
     * @return Locals size.
     */
    @EqualsAndHashCode.Include
    public int locals() {
        return (int) new XmlOperand(
            this.node.children().collect(Collectors.toList()).get(1)
        ).asObject();
    }

    /**
     * Prestructor.
     *
     * @param stack Stack size.
     * @param locals Locals size.
     * @return XML node that represents Max Stack and Max Locals.
     */
    private static XmlNode prestructor(final int stack, final int locals) {
        return new XmlNode(new Xembler(new DirectivesMaxs(stack, locals)).xmlQuietly());
    }
}
