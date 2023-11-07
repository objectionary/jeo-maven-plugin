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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.objectweb.asm.Label;

/**
 * XML representation of bytecode label.
 * @since 0.1
 */
public final class XmlLabel implements XmlCommand {

    /**
     * All Labels.
     * @todo #234:90min Hide LABELS Public Static Variable.
     *  It is not a good idea to expose LABELS and use static variable in general.
     *  We definitely have to hide this variable or, what is even better, get rid of it and create
     *  one more class that will encapsulate the LABELS logic.
     * @checkstyle StaticVariableNameCheck (3 lines)
     */
    public static final Map<String, Label> LABELS = new ConcurrentHashMap<>();

    /**
     * Label node.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param node Label node.
     */
    XmlLabel(final XmlNode node) {
        this.node = node;
    }

    @Override
    public void writeTo(final BytecodeMethod method) {
        final String id = this.node.child("base", "string").text();
        XmlLabel.LABELS.putIfAbsent(id, new Label());
        method.markLabel(XmlLabel.LABELS.get(id));
    }
}
