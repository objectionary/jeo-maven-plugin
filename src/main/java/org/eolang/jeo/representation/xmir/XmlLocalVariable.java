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

import java.util.Optional;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.eolang.jeo.representation.bytecode.LocalVariable;
import org.objectweb.asm.Label;

/**
 * Xml representation of a local variable.
 * @since 0.6
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class XmlLocalVariable {

    /**
     * XML node of local variable.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param node XML node.
     */
    XmlLocalVariable(final XmlNode node) {
        this.node = node;
    }

    /**
     * Convert to domain attribute.
     * @return Attribute.
     */
    public BytecodeAttribute attribute() {
        return new LocalVariable(
            this.index(),
            this.name(),
            this.descriptor(),
            this.signature(),
            this.start(),
            this.end()
        );
    }

    /**
     * Local variable index.
     * @return Local variable index.
     */
    private int index() {
        return this.integer(0);
    }

    /**
     * Name.
     * @return Name.
     */
    private String name() {
        return this.string(1);
    }

    /**
     * Descriptor.
     * @return Descriptor.
     */
    private String descriptor() {
        return this.string(2);
    }

    /**
     * Local variable signature.
     * @return Signature.
     */
    private String signature() {
        return this.string(3);
    }

    /**
     * Start label.
     * @return Label.
     */
    private Label start() {
        return this.label(4);
    }

    /**
     * End label.
     * @return Label.
     */
    private Label end() {
        return this.label(5);
    }

    /**
     * Get integer by index.
     * @param index Index.
     * @return Integer.
     */
    private int integer(final int index) {
        return this.operand(index).map(Integer.class::cast).orElse(0);
    }

    /**
     * Get string by index.
     * @param index Index.
     * @return String.
     */
    private String string(final int index) {
        return this.operand(index).map(String.class::cast).orElse(null);
    }

    /**
     * Get label by index.
     * @param index Index.
     * @return Label.
     */
    private Label label(final int index) {
        return this.operand(index).map(Label.class::cast).orElse(null);
    }

    /**
     * Get operand by index.
     * @param index Index.
     * @return Optional operand.
     */
    private Optional<Object> operand(final int index) {
        return Optional.ofNullable(this.node.children().collect(Collectors.toList()).get(index))
            .map(XmlOperand::new)
            .map(XmlOperand::asObject);
    }
}
