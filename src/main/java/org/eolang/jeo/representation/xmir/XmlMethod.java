/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.MethodName;
import org.eolang.jeo.representation.NumberedName;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeAttributes;
import org.eolang.jeo.representation.bytecode.BytecodeMaxs;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameters;
import org.eolang.jeo.representation.bytecode.BytecodeMethodProperties;
import org.eolang.jeo.representation.directives.JeoFqn;

/**
 * XML method.
 *
 * @since 0.1
 */
public final class XmlMethod {

    /**
     * Field base full qualified name.
     */
    private static final String METHOD = new JeoFqn("method").fqn();

    /**
     * Method node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     *
     * @param xmlnode Method node
     */
    XmlMethod(final XmlNode xmlnode) {
        this(new XmlJeoObject(xmlnode));
    }

    /**
     * Constructor.
     *
     * @param node Method node
     */
    private XmlMethod(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Convert to bytecode.
     *
     * @return Bytecode method
     */
    public BytecodeMethod bytecode() {
        try {
            return new BytecodeMethod(
                this.trycatchEntries()
                    .stream()
                    .map(XmlTryCatchEntry::bytecode)
                    .collect(Collectors.toList()),
                this.instructions()
                    .stream()
                    .map(XmlBytecodeEntry::bytecode)
                    .collect(Collectors.toList()),
                this.annotations(),
                this.properties(),
                this.defvalue()
                    .map(XmlDefaultValue::bytecode)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList()),
                this.maxs().map(XmlMaxs::bytecode)
                    .orElse(new BytecodeMaxs()),
                this.attrs()
            );
        } catch (final IllegalStateException exception) {
            throw new ParsingException(
                String.format(
                    "Unexpected exception during parsing the method '%s'",
                    this.name()
                ),
                exception
            );
        } catch (final IllegalArgumentException exception) {
            throw new ParsingException(
                String.format(
                    "Can't transform method '%s' to bytecode, node: '%s",
                    this.name(),
                    this.node
                ),
                exception
            );
        } catch (final IndexOutOfBoundsException exception) {
            throw new ParsingException(
                String.format(
                    "Can't transform method '%s' to bytecode, because of index out of bounds, most probably due to broken XMIR",
                    this.name()
                ),
                exception
            );
        }
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof XmlMethod) {
            result = Objects.equals(this.node, ((XmlMethod) other).node);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.node);
    }

    @Override
    public String toString() {
        return String.format("XmlMethod(node=%s)", this.node);
    }

    /**
     * Whether the node is a method.
     *
     * @return True if the node is a method
     */
    boolean isMethod() {
        return this.node.base().map(XmlMethod.METHOD::equals).orElse(false);
    }

    private BytecodeAttributes attrs() {
        return this.node.children()
            .map(XmlSeq::new)
            .filter(XmlSeq::named)
            .filter(seq -> seq.name().contains("local-variable-table"))
            .findFirst()
            .map(XmlAttributes::new)
            .map(XmlAttributes::attributes)
            .orElseGet(BytecodeAttributes::new);
    }

    private String name() {
        return this.node.children()
            .map(XmlNamedObject::new)
            .filter(XmlNamedObject::named)
            .filter(xml -> "name".equals(xml.name()))
            .map(XmlValue::new)
            .map(XmlValue::string)
            .map(NumberedName::new)
            .map(NumberedName::plain)
            .map(MethodName::new)
            .map(MethodName::bytecode)
            .findFirst().orElseThrow(
                () -> new IllegalStateException(
                    String.format("Method '%s' doesn't have a name", this.node.name())
                )
            );
    }

    private List<XmlBytecodeEntry> instructions() {
        return this.node.children()
            .map(XmlSeq::new)
            .filter(XmlSeq::named)
            .filter(seq -> seq.name().contains("body"))
            .findFirst()
            .map(XmlSeq::children)
            .orElse(Stream.empty())
            .map(XmlMethod::toEntry)
            .collect(Collectors.toList());
    }

    private static XmlBytecodeEntry toEntry(final XmlNode node) {
        final XmlBytecodeEntry result;
        final Optional<String> base = new XmlJeoObject(node).base();
        if (base.isPresent() && new JeoFqn("label").fqn().equals(base.get())) {
            result = new XmlLabel(node);
        } else if (base.isPresent() && new JeoFqn("frame").fqn().equals(base.get())) {
            result = new XmlFrame(node);
        } else if (base.isPresent() && new JeoFqn("line-number").fqn().equals(base.get())) {
            result = new XmlLine(node);
        } else {
            result = new XmlInstruction(node);
        }
        return result;
    }

    private Optional<XmlMaxs> maxs() {
        return this.node.children()
            .map(XmlJeoObject::new)
            .filter(XmlJeoObject::named)
            .filter(object -> object.name().contains("maxs"))
            .findFirst()
            .map(XmlMaxs::new);
    }

    private BytecodeMethodProperties properties() {
        try {
            return new BytecodeMethodProperties(
                this.access(),
                this.name(),
                this.descriptor(),
                this.signature(),
                this.params(),
                this.exceptions()
            );
        } catch (final IllegalStateException exception) {
            throw new ParsingException(
                String.format(
                    "Unexpected exception during parsing the method '%s' properties",
                    this.name()
                ),
                exception
            );
        }
    }

    private int access() {
        return (int) new XmlValue(this.child("access")).object();
    }

    private String descriptor() {
        return new XmlValue(this.child("descriptor")).string();
    }

    private String signature() {
        return new XmlValue(this.child("signature")).string();
    }

    private XmlNamedObject child(final String name) {
        return this.node.children()
            .map(XmlNamedObject::new)
            .filter(XmlNamedObject::named)
            .filter(object -> object.name().contains(name))
            .findFirst().orElseThrow(
                () -> new IllegalStateException(
                    String.format(
                        "Method '%s' doesn't have '%s' child",
                        this.name(),
                        name
                    )
                )
            );
    }

    private List<XmlTryCatchEntry> trycatchEntries() {
        return this.node.children()
            .map(XmlSeq::new)
            .filter(XmlSeq::named)
            .filter(seq -> seq.name().contains("trycatchblocks"))
            .flatMap(XmlSeq::children)
            .map(XmlTryCatchEntry::new)
            .collect(Collectors.toList());
    }

    private BytecodeAnnotations annotations() {
        return this.node.children()
            .map(XmlJeoObject::new)
            .filter(XmlJeoObject::named)
            .filter(object -> object.name().contains("annotations"))
            .findFirst()
            .map(XmlAnnotations::new)
            .map(XmlAnnotations::bytecode)
            .orElse(new BytecodeAnnotations());
    }

    private Optional<XmlDefaultValue> defvalue() {
        return this.node.children()
            .map(XmlDefaultValue::new)
            .filter(XmlDefaultValue::isDefaultValue)
            .findFirst();
    }

    private BytecodeMethodParameters params() {
        return this.node.children()
            .map(XmlMethodParams::new)
            .filter(XmlMethodParams::isParams)
            .findFirst()
            .map(XmlMethodParams::params)
            .orElse(new BytecodeMethodParameters());
    }

    private String[] exceptions() {
        return this.node.children()
            .map(XmlExceptions::new)
            .filter(XmlExceptions::isExceptions)
            .findFirst()
            .map(n -> n.bytecode().stream())
            .orElse(Stream.empty())
            .toArray(String[]::new);
    }
}
