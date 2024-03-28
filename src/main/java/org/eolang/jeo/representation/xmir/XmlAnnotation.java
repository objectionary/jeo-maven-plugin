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

import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotationProperty;

/**
 * Xmir representation of an annotation.
 * @since 0.1
 */
public class XmlAnnotation {

    /**
     * Xmir node.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param xmlnode XML node.
     */
    public XmlAnnotation(final XmlNode xmlnode) {
        this.node = xmlnode;
    }

    /**
     * Annotation descriptor.
     * @return Descriptor.
     */
    public String descriptor() {
        return new HexString(this.node.child("name", "descriptor").text()).decode();
    }

    /**
     * Annotation visible.
     * Is it runtime-visible?
     * @return True if visible at runtime, false otherwise.
     */
    public boolean visible() {
        return new HexString(this.node.child("name", "visible").text()).decodeAsBoolean();
    }

    /**
     * Annotation properties.
     * @return Properties.
     */
    public List<BytecodeAnnotationProperty> props() {
        return this.node.children()
            .filter(xmlnode -> xmlnode.hasAttribute("base", "annotation-property"))
            .map(XmlAnnotationProperty::new)
            .map(XmlAnnotationProperty::toBytecode)
            .collect(Collectors.toList());
    }
}
