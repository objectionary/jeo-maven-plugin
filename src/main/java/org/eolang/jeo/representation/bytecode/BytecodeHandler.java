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
package org.eolang.jeo.representation.bytecode;

import org.objectweb.asm.Handle;

/**
 * Bytecode handler.
 * @since 0.6
 */
public final class BytecodeHandler {

    /**
     * Handler tag.
     */
    private final int tag;

    /**
     * Owner.
     */
    private final String owner;

    /**
     * Name.
     */
    private final String name;

    /**
     * Descriptor.
     */
    private final String descriptor;

    /**
     * Is it an interface?
     */
    private final boolean interf;

    /**
     * Constructor.
     * @param tag Tag.
     * @param owner Owner.
     * @param name Name.
     * @param descriptor Descriptor.
     * @param interf Is it an interface?
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public BytecodeHandler(
        final int tag,
        final String owner,
        final String name,
        final String descriptor,
        final boolean interf
    ) {
        this.tag = tag;
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
        this.interf = interf;
    }

    /**
     * Convert to a handler.
     * @return Handler.
     */
    public Handle asHandle() {
        return new Handle(this.tag, this.owner, this.name, this.descriptor, this.interf);
    }

}
