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
import org.eolang.jeo.representation.xmir.XmlClass;
import org.eolang.jeo.representation.xmir.XmlMethod;

/**
 * This class tries to combine constructors of the decorated class and the decorator.
 * @since 0.1
 * @todo #252:90min The first attempt to inline constructors is failed.
 *  The constructor inlining is not a straightforward task, since we need to take into
 *  account the order of arguments and the order of instructions in the constructor.
 *  Sine we decided to implement inlining using high-level XML representation, maybe it makes sence
 *  just remove all the related classes to this optimization.
 */
class DecoratorConstructors {

    /**
     * Decorated class.
     */
    private final XmlClass decorated;

    /**
     * Constructor.
     * @param decorated Decorated class.
     */
    DecoratorConstructors(
        final XmlClass decorated
    ) {
        this.decorated = decorated;
    }

    /**
     * Returns a list of constructors of the decorator class.
     * @return List of constructors.
     */
    List<XmlMethod> constructors() {
        return this.decorated.constructors();
    }
}
