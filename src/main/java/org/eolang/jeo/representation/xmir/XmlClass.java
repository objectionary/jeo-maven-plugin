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

import com.jcabi.xml.XMLDocument;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.directives.DirectivesClass;
import org.w3c.dom.Node;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * XML class.
 * @since 0.1
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class XmlClass {

    /**
     * Class node from entire XML.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    private final Node node;

    /**
     * Constructor.
     * @param classname Class name.
     */
    XmlClass(final String classname) {
        this(
            new XMLDocument(
                new Xembler(
                    new DirectivesClass(classname),
                    new Transformers.Node()
                ).xmlQuietly()
            ).node().getFirstChild()
        );
    }

    /**
     * Constructor.
     * @param xml Class node.
     */
    XmlClass(final Node xml) {
        this.node = xml;
    }

    /**
     * Class name.
     * @return Name.
     */
    public String name() {
        return String.valueOf(this.node.getAttributes().getNamedItem("name").getTextContent());
    }

    @Override
    public String toString() {
        return new XMLDocument(this.node).toString();
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other == null || this.getClass() != other.getClass()) {
            result = false;
        } else {
            result = Objects.equals(
                new XMLDocument(this.node),
                new XMLDocument(((XmlClass) other).node)
            );
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.node);
    }

    /**
     * Retrieve all constructors from XMIR.
     * @return List of constructors.
     */
    List<XmlMethod> constructors() {
        return this.methods()
            .stream()
            .filter(XmlMethod::isConstructor)
            .collect(Collectors.toList());
    }

    /**
     * Methods.
     * @return Class methods.
     */
    List<XmlMethod> methods() {
        return this.objects()
            .filter(o -> o.attribute("base").isEmpty())
            .map(XmlMethod::new)
            .collect(Collectors.toList());
    }

    /**
     * Fields.
     * @return Class fields.
     */
    List<XmlField> fields() {
        return this.objects()
            .filter(o -> o.attribute("base").isPresent())
            .filter(o -> "field".equals(o.attribute("base").get()))
            .map(XmlField::new)
            .collect(Collectors.toList());
    }

    /**
     * Class properties.
     * @return Class properties.
     */
    XmlClassProperties properties() {
        return new XmlClassProperties(this.node);
    }

    /**
     * Objects.
     * @return Stream of class objects.
     */
    private Stream<XmlNode> objects() {
        return new XmlNode(this.node).children();
    }
}
