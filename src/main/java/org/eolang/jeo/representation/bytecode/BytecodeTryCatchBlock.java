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

import com.jcabi.log.Logger;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesTryCatch;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.xembly.Directive;

/**
 * Bytecode try-catch block.
 * @since 0.1
 */
@ToString
@EqualsAndHashCode
public final class BytecodeTryCatchBlock implements BytecodeEntry {

    /**
     * Start label.
     */
    private final Label start;

    /**
     * End label.
     */
    private final Label end;

    /**
     * Handler label.
     */
    private final Label handler;

    /**
     * Exception type.
     */
    private final String type;

    /**
     * Constructor.
     * @param startlabel Start label.
     * @param endlabel End label.
     * @param handlerlabel Handler label.
     * @param exception Exception type.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public BytecodeTryCatchBlock(
        final Label startlabel,
        final Label endlabel,
        final Label handlerlabel,
        final String exception
    ) {
        this.start = startlabel;
        this.end = endlabel;
        this.handler = handlerlabel;
        this.type = exception;
    }

    @Override
    public void writeTo(final MethodVisitor visitor) {
        Logger.debug(
            this,
            String.format(
                "Writing try-catch entry into the method with the following values: start=%s, end=%s, handler=%s, type=%s",
                this.start,
                this.end,
                this.handler,
                this.type
            )
        );
        visitor.visitTryCatchBlock(this.start, this.end, this.handler, this.type);
    }

    @Override
    public Iterable<Directive> directives(final boolean counting) {
        return new DirectivesTryCatch(this.start, this.end, this.handler, this.type);
    }

    @Override
    public boolean isLabel() {
        return false;
    }

    @Override
    public boolean isOpcode() {
        return false;
    }

    @Override
    public String testCode() {
        return "// Try-catch blocks are not supported in tests yet";
    }
}
