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

import com.jcabi.matchers.XhtmlMatchers;
import java.util.Arrays;
import java.util.Collections;
import org.eolang.jeo.matchers.SameXml;
import org.eolang.jeo.representation.directives.DirectivesMethodParams;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Bytecode method parameters.
 * @since 0.6
 */
final class BytecodeMethodParametersTest {

    @Test
    void convertsToDirectivesWithTwoParams() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't convert bytecode method parameters to correct directives",
            new Xembler(new BytecodeMethodParameters(
            ).directives("(II)V")).xml(),
            XhtmlMatchers.hasXPaths(
                "/o[@base='params']",
                "/o[@base='params']/o[@base='param' and @name='param-SQ==-0']",
                "/o[@base='params']/o[@base='param' and @name='param-SQ==-1']"
            )
        );
    }

}