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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML field.
 * @since 0.1
 */
public class XmlField {

    /**
     * Field node.
     */
    private final Node node;

    /**
     * Constructor.
     * @param node Field node.
     */
    XmlField(final Node node) {
        this.node = node;
    }

    public String name() {
        return node.getAttributes().getNamedItem("name").getNodeValue();
    }

    public String descriptor() {
        return new HexString(
            children()
                .filter(n -> n.getAttributes().getNamedItem("name").getNodeValue().equals("descriptor"))
                .findFirst()
                .orElseThrow(IllegalStateException::new)
                .getTextContent()).decode();
    }

    public int access() {
        return 0;
    }

    public String signature() {
        return "null";
    }

    public Object value() {
        return "null";
    }

    private Stream<Node> children() {
        final NodeList childs = this.node.getChildNodes();
        final List<Node> res = new ArrayList<>(childs.getLength());
        for (int identifier = 0; identifier < childs.getLength(); identifier++) {
            res.add(childs.item(identifier));
        }
        return res.stream();
    }

}
