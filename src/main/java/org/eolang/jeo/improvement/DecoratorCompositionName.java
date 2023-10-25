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

import org.eolang.jeo.Representation;
import org.eolang.jeo.representation.HexData;

/**
 * The name of a decorator composition.
 * <p>Example:</p>
 * <ul>
 *     <li>decorated: A, decorator: B => name: A$B</li>
 *     <li>decorated: Foo, decorator: Bar => name: Foo$Bar</li>
 *     <li>decorated: org/eolang/Foo, decorator: org/eolang/Bar => name: org/eolang/Foo$Bar</li>
 *     <li>decorated: a/Foo, decorator: b/Bar => name: b/Foo$Bar</li>
 * </ul>
 * Pay attention that we replace periods with slashes. This class also can convert the final name
 * into hexadecimal representation.
 * @since 0.1
 */
final class DecoratorCompositionName {

    /**
     * Decorated class.
     */
    private final Representation decorated;

    /**
     * Class that decorates.
     */
    private final Representation decorator;

    /**
     * Constructor.
     * @param decorated Decorated class
     * @param decorator Class that decorates
     */
    DecoratorCompositionName(
        final Representation decorated,
        final Representation decorator
    ) {
        this.decorated = decorated;
        this.decorator = decorator;
    }

    /**
     * Generated name for the decorator composition.
     * @return Name.
     */
    String value() {
        final String left = this.decorated.name();
        final String right = this.decorator.name();
        final String prefix;
        if (right.contains("/")) {
            prefix = right.substring(0, right.lastIndexOf('/') + 1);
        } else {
            prefix = "";
        }
        return String.format(
            "%s%s$%s",
            prefix,
            left.substring(left.lastIndexOf('/') + 1),
            right.substring(right.lastIndexOf('/') + 1)
        );
    }

    /**
     * Hexadecimal representation of the generated name.
     * @return Hexadecimal representation of the name.
     */
    String hex() {
        return new HexData(this.value()).value();
    }
}
