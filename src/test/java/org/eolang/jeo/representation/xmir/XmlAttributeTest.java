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

import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.eolang.jeo.representation.bytecode.InnerClass;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlAttribute}.
 * @since 0.6
 */
final class XmlAttributeTest {

    @Test
    void convertsToDomainInnerClass() throws ImpossibleModificationException {
        final BytecodeAttribute expected = new InnerClass("name", "outer", "inner", 0);
        MatcherAssert.assertThat(
            "We expect the attribute to be converted to a correct  domain attribute (inner class)",
            new XmlAttribute(new Xembler(expected.directives()).xml()).attribute(),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void throwsExceptionIfCannotIdentifyAttribute() {
        MatcherAssert.assertThat(
            "We expect an exception message be understandable and clear",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new XmlAttribute("<o base='unknown'/>").attribute(),
                "We expect an exception to be thrown if the attribute cannot be identified"
            ).getMessage(),
            Matchers.equalTo("Unknown attribute base 'unknown'")
        );
    }
}
