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
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.objectweb.asm.Handle;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives Handle.
 * Xmir representation of the Java ASM Handle object.
 * @since 0.1
 * @todo #329:30min Implement DirectivesHandle class.
 *  The {@link Handle} class is one of the parameters for INVOKEDYNAMIC instruction.
 *  The class should be implemented in the same way as {@link DirectivesLabel}.
 *  Don't forget to add tests.
 */
public final class DirectivesHandle implements Iterable<Directive> {

    /**
     * ASM Handle object.
     */
    private final Handle handle;

    /**
     * Constructor.
     * @param handle ASM Handle object.
     */
    public DirectivesHandle(final Handle handle) {
        this.handle = handle;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new Directives()
            .add("o")
            .attr("base", "handle")
            .append(new DirectivesData(this.handle.getTag()))
            .append(new DirectivesData(this.handle.getOwner()))
            .append(new DirectivesData(this.handle.getName()))
            .append(new DirectivesData(this.handle.getDesc()))
            .append(new DirectivesData(this.handle.isInterface()))
            .up()
            .iterator();
    }
}
