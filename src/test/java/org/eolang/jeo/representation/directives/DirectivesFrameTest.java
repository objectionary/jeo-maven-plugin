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
package org.eolang.jeo.representation.directives;

import org.eolang.jeo.representation.bytecode.BytecodeFrame;
import org.eolang.jeo.representation.xmir.NativeXmlNode;
import org.eolang.jeo.representation.xmir.XmlFrame;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesFrame}.
 *
 * @since 0.3
 */
final class DirectivesFrameTest {

    @Test
    void createsCorrectDirectivesForFrame() throws ImpossibleModificationException {
        final int type = Opcodes.F_NEW;
        final int nlocal = 2;
        final Object[] locals = {"java/lang/Object", Opcodes.LONG};
        final int nstack = 3;
        final Object[] stack = {"java/lang/String", Opcodes.DOUBLE};
        final String xml = new Xembler(
            new DirectivesFrame(type, nlocal, locals, nstack, stack)
        ).xml();
        MatcherAssert.assertThat(
            "We failed to create correct directives for bytecode frame.",
            new XmlFrame(new NativeXmlNode(xml)).bytecode(),
            Matchers.equalTo(new BytecodeFrame(type, nlocal, locals, nstack, stack))
        );
    }
}
