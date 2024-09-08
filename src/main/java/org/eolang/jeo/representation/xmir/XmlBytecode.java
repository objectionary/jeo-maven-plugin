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

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.PrefixedName;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.bytecode.BytecodeClass;

/**
 * XML to Java bytecode.
 * @since 0.1.0
 */
public final class XmlBytecode {

    /**
     * XML.
     */
    private final XML xml;

    /**
     * Verify bytecode.
     */
    private final boolean verify;

    /**
     * Constructor.
     * @param xml XML.
     * @param verify Verify bytecode.
     */
    public XmlBytecode(final XML xml, final boolean verify) {
        this.xml = xml;
        this.verify = verify;
    }

    /**
     * Constructor.
     * @param lines Xml document lines.
     */
    XmlBytecode(final String... lines) {
        this(new XMLDocument(String.join("\n", lines)));
    }

    /**
     * Constructor.
     * @param xml XML.
     */
    private XmlBytecode(final XML xml) {
        this(xml, true);
    }

    /**
     * Traverse XML and build bytecode class.
     * @return Bytecode.
     */
    public Bytecode bytecode() {
        final XmlProgram program = new XmlProgram(this.xml);
        final XmlClass clazz = program.top();
        final BytecodeClass bytecode = new BytecodeClass(
            new ClassName(program.pckg(), new PrefixedName(clazz.name()).decode()).full(),
            clazz.properties().bytecode(),
            this.verify
        );
        clazz.annotations().ifPresent(bytecode::withAnnotations);
        for (final XmlField field : clazz.fields()) {
            bytecode.withField(field.bytecode());
        }
        for (final XmlMethod xmlmethod : clazz.methods()) {
            bytecode.withMethod(xmlmethod.bytecode(bytecode));
        }
        clazz.attributes().ifPresent(attrs -> attrs.writeTo(bytecode));
        return bytecode.bytecode();
    }
}
