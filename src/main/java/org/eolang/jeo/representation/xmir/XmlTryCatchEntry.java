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

import java.util.Optional;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.BytecodeTryCatchBlock;

/**
 * XML try-catch entry.
 * @since 0.1
 */
public final class XmlTryCatchEntry implements XmlBytecodeEntry {

    /**
     * XML node.
     */
    private final XmlNode xmlnode;

    private final AllLabels labels;

    public XmlTryCatchEntry(final XmlNode xmlnode) {
        this(xmlnode, new AllLabels());
    }

    /**
     * Constructor.
     * @param node XML node
     */
    public XmlTryCatchEntry(final XmlNode node, final AllLabels labels) {
        this.xmlnode = node;
        this.labels = labels;
    }

    @Override
    public void writeTo(final BytecodeMethod method) {
        method.trycatch(
            new BytecodeTryCatchBlock(
                this.label("start").map(this.labels::label).orElse(null),
                this.label("end").map(this.labels::label).orElse(null),
                this.label("handler").map(this.labels::label).orElse(null),
                this.type().map(HexString::new).map(HexString::decode).orElse(null)
            )
        );
    }

    /**
     * Retrieves the label.
     * @param name Label uid.
     * @return Label.
     */
    Optional<String> label(final String name) {
        return this.xmlnode.optchild("name", name)
            .map(XmlNode::text)
            .map(HexString::new)
            .map(HexString::decode);
    }

    /**
     * Retrieves the exception type.
     * @return Exception type.
     */
    Optional<String> type() {
        return this.xmlnode.optchild("name", "type").map(XmlNode::text);
    }
}
