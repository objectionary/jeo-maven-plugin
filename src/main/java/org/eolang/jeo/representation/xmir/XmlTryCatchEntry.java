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
package org.eolang.jeo.representation.xmir;

import java.util.Optional;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeTryCatchBlock;
import org.eolang.jeo.representation.directives.EoFqn;
import org.objectweb.asm.Label;

/**
 * XML try-catch entry.
 * @since 0.1
 */
public final class XmlTryCatchEntry implements XmlBytecodeEntry {

    /**
     * XML node.
     */
    private final XmlNode xmlnode;

    /**
     * Method Labels.
     */
    private final AllLabels labels;

    /**
     * Constructor.
     * @param xmlnode XML node
     */
    public XmlTryCatchEntry(final XmlNode xmlnode) {
        this(xmlnode, new AllLabels());
    }

    /**
     * Constructor.
     * @param node XML node
     * @param labels Labels
     */
    XmlTryCatchEntry(final XmlNode node, final AllLabels labels) {
        this.xmlnode = node;
        this.labels = labels;
    }

    /**
     * Converts XML to bytecode.
     * @return Bytecode try-catch block.
     */
    public BytecodeTryCatchBlock bytecode() {
        return new BytecodeTryCatchBlock(this.start(), this.end(), this.handler(), this.type());
    }

    /**
     * Retrieves the start label.
     * @return Start label.
     */
    private Label start() {
        return this.label(0).map(this.labels::label).orElse(null);
    }

    /**
     * Retrieves the end label.
     * @return End label.
     */
    private Label end() {
        return this.label(1).map(this.labels::label).orElse(null);
    }

    /**
     * Retrieves the handler label.
     * @return Handler label.
     */
    private Label handler() {
        return this.label(2).map(this.labels::label).orElse(null);
    }

    /**
     * Retrieves the exception type.
     * @return Exception type.
     */
    private String type() {
        return Optional.ofNullable(this.xmlnode.children().collect(Collectors.toList()).get(3))
            .map(XmlValue::new)
            .map(XmlValue::bytes)
            .map(XmlBytes::hex)
            .map(HexString::decode)
//            .map(XmlNode::text)
//            .map(HexString::new)
//            .map(HexString::decode)
            .filter(s -> !s.isEmpty())
            .orElse(null);
    }

    /**
     * Retrieves the label.
     * @param id Label uid.
     * @return Label.
     */
    private Optional<String> label(final int id) {
        return Optional.ofNullable(this.xmlnode.children().collect(Collectors.toList()).get(id))
            .filter(node -> !node.hasAttribute("base", new EoFqn("nop").fqn()))
            .map(XmlValue::new)
            .map(XmlValue::bytes)
            .map(XmlBytes::hex)
//            .map(XmlNode::text)
//            .map(HexString::new)
            .map(HexString::decode);
    }
}
