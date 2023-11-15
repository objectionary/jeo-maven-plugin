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
package org.eolang.jeo.improvement;

import java.util.List;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.xmir.XmlClass;
import org.eolang.jeo.representation.xmir.XmlMethod;
import org.eolang.jeo.representation.xmir.XmlProgram;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link DecoratorConstructors}.
 * @since 0.1
 * @todo #164:90min Write tests that can understand java.
 *  Currently writing tests is pain, since we have to operate on XML and bytecode.
 *  We need to write tests that would contain initial java code and expected java code.
 *  Under the hood, we should compile initial java code, apply all out transformations
 *  and then compare the result with expected java code.
 */
class DecoratorConstructorsTest {

    @Test
    @SuppressWarnings("PMD.UnusedLocalVariable")
    void combinesConstructors() {
        final XmlClass decorated = new XmlProgram(
            new BytecodeClass("Foo")
                .withConstructor("(I)V").instruction(Opcodes.RETURN).up()
                .withConstructor("(Ljava/lang/String;)V").instruction(Opcodes.RETURN).up()
                .xml()
        ).top();
        final XmlClass decorator = new XmlProgram(
            new BytecodeClass("Bar")
                .withConstructor("(LFoo;D)V").instruction(Opcodes.RETURN).up()
                .withConstructor("(LFoo;)V").instruction(Opcodes.RETURN).up()
                .xml()
        ).top();
        final List<XmlMethod> res = new DecoratorConstructors(decorated, decorator)
            .constructors();
        final List<XmlMethod> expected = new XmlProgram(
            new BytecodeClass("Expected")
                .withConstructor("(ID)V").instruction(Opcodes.RETURN).up()
                .withConstructor("(I)V").instruction(Opcodes.RETURN).up()
                .withConstructor("(Ljava/lang/String;D)V").instruction(Opcodes.RETURN).up()
                .withConstructor("(Ljava/lang/String;)V").instruction(Opcodes.RETURN).up()
                .xml()
        ).top().constructors();
        //@checkstyle MethodBodyCommentsCheck (5 lines)
        //@todo #164:90min Check content of each constructor.
        // Currently we only check the size of the list. We need to check the content of each
        // constructor to be sure that each constructor contains the same instructions as expected.
        MatcherAssert.assertThat(
            "DecoratorConstructors should combine constructors from decorated and decorator classes, but it didn't",
            res,
            Matchers.hasSize(4)
        );
    }
}
