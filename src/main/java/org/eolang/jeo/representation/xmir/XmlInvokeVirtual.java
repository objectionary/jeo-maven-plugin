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

import java.util.Collections;
import java.util.List;

/**
 * Abstraction over invoke virtual call.
 * @since 0.1
 */
public class XmlInvokeVirtual {

    /**
     * Instructions.
     */
    private final List<XmlInstruction> instructions;

    /**
     * Constructor.
     * @param instructions Instructions.
     */
    XmlInvokeVirtual(final List<XmlInstruction> instructions) {
        this.instructions = instructions;
    }

    /**
     * GETFIELD instruction.
     * @return Instruction.
     */
    XmlInstruction field() {
        return this.instructions.get(0);
    }

    /**
     * INVOKEVIRTUAL instruction.
     * @return Instruction.
     */
    XmlInstruction invocation() {
        return this.instructions.get(this.instructions.size() - 1);
    }

    /**
     * Get field name.
     * @return Field name.
     */
    String fieldName() {
        return String.valueOf(this.field().arguments()[1]);
    }

    /**
     * Get field type.
     * @return Field type.
     */
    String fieldType() {
        return String.valueOf(this.field().arguments()[2]);
    }

    /**
     * Get method name.
     * @return Method name.
     */
    String methodName() {
        return String.valueOf(this.invocation().arguments()[1]);
    }

    /**
     * Get method arguments.
     * @return Method arguments.
     */
    List<XmlInstruction> arguments() {
        final List<XmlInstruction> result;
        if (this.instructions.size() < 3) {
            result = Collections.emptyList();
        } else {
            result = this.instructions.subList(1, this.instructions.size() - 1);
        }
        return result;
    }
}
