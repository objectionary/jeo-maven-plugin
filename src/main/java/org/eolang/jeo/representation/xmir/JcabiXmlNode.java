/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.lints.Defect;
import org.eolang.lints.Source;
import org.eolang.parser.StrictXmir;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Jcabi XML node.
 * @since 0.8
 */
@ToString
@EqualsAndHashCode
@SuppressWarnings("PMD.TooManyMethods")
public final class JcabiXmlNode implements XmlNode {

    /**
     * Set of ignored defects.
     */
    private static final Collection<String> IGNORE = new HashSet<>(
        Arrays.asList(
            "no-attribute-formation",
            "redundant-object",
            "duplicate-names-in-diff-context",
            "unit-test-missing"
        )
    );

    /**
     * XML document.
     */
    private final XML doc;

    /**
     * Ctor.
     * @param xml XML string.
     */
    JcabiXmlNode(final String... xml) {
        this(new XMLDocument(String.join("\n", xml)).inner().getFirstChild());
    }

    /**
     * Ctor.
     * @param item XML node.
     */
    JcabiXmlNode(final Node item) {
        this(new XMLDocument(item));
    }

    /**
     * Ctor.
     * @param root XML document.
     */
    JcabiXmlNode(final XML root) {
        this.doc = root;
    }

    @Override
    public Stream<XmlNode> children() {
        return this.objects().map(JcabiXmlNode::new);
    }

    @Override
    public String text() {
        return this.doc.inner().getTextContent();
    }

    @Override
    public Optional<String> attribute(final String name) {
        Optional<String> result = Optional.empty();
        final NamedNodeMap attributes = this.doc.inner().getAttributes();
        final int length = attributes.getLength();
        for (int index = 0; index < length; ++index) {
            final Node item = attributes.item(index);
            if (item.getNodeName().startsWith(name)) {
                result = Optional.of(item.getTextContent());
                break;
            }
        }
        return result;
    }

    @Override
    public XmlNode child(final String name) {
        return this.optchild(name).orElseThrow(() -> this.notFound(name));
    }

    @Override
    public List<String> xpath(final String xpath) {
        return this.doc.xpath(xpath);
    }

    @Override
    public void validate() {
        final Collection<Defect> defects = new Source(new StrictXmir(this.doc)).defects()
            .stream()
            .filter(defect -> !defect.experimental())
            .filter(JcabiXmlNode::notIgnored)
            .collect(Collectors.toList());
        if (!defects.isEmpty()) {
            throw new IllegalStateException(
                String.format(
                    "XMIR is incorrect: %s, %n%s%n",
                    defects.stream().map(Object::toString).collect(Collectors.joining("\n")),
                    this.doc
                )
            );
        }
    }

    /**
     * Get optional child node.
     * @param name Child node name.
     * @return Child node.
     */
    private Optional<XmlNode> optchild(final String name) {
        Optional<XmlNode> result = Optional.empty();
        final NodeList children = this.doc.inner().getChildNodes();
        final int length = children.getLength();
        for (int index = 0; index < length; ++index) {
            final Node current = children.item(index);
            if (current.getNodeName().equals(name)) {
                result = Optional.of(new JcabiXmlNode(current));
                break;
            }
        }
        return result;
    }

    /**
     * Generate exception if element not found.
     * @param name Element name.
     * @return Exception.
     */
    private IllegalStateException notFound(final String name) {
        return new IllegalStateException(
            String.format(
                "Can't find %s in '%s'",
                name,
                this.doc
            )
        );
    }

    /**
     * Objects.
     * @return Stream of class objects.
     */
    private Stream<Node> objects() {
        final NodeList children = this.doc.inner().getChildNodes();
        final List<Node> res = new ArrayList<>(children.getLength());
        for (int index = 0; index < children.getLength(); ++index) {
            final Node child = children.item(index);
            if ("o".equals(child.getNodeName())) {
                res.add(child);
            }
        }
        return res.stream();
    }

    /**
     * Check if the defect is not ignored.
     * @param defect Defect to check.
     * @return True if the defect is not ignored, false otherwise.
     */
    private static boolean notIgnored(final Defect defect) {
        return !JcabiXmlNode.IGNORE.contains(defect.rule().split(" ")[0]);
    }
}
