/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.MethodName;
import org.eolang.jeo.representation.NumberedName;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeAttributes;
import org.eolang.jeo.representation.bytecode.BytecodeMaxs;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameters;
import org.eolang.jeo.representation.bytecode.BytecodeMethodProperties;
import org.eolang.jeo.representation.directives.DirectivesMaxs;
import org.eolang.jeo.representation.directives.DirectivesMethod;
import org.eolang.jeo.representation.directives.DirectivesMethodParams;
import org.eolang.jeo.representation.directives.DirectivesMethodProperties;
import org.eolang.jeo.representation.directives.Format;
import org.eolang.jeo.representation.directives.JeoFqn;
import org.objectweb.asm.Opcodes;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * XML method.
 *
 * @since 0.1
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods"})
@ToString
@EqualsAndHashCode
public final class XmlMethod {

    /**
     * Field base full qualified name.
     */
    private static final String METHOD = new JeoFqn("method").fqn();

    /**
     * Undefined value.
     * Max stack and locals need to be recomputed.
     */
    private static final int UNDEFINED = -1;

    /**
     * Method node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param xmlnode Method node.
     */
    XmlMethod(final XmlNode xmlnode) {
        this(new XmlJeoObject(xmlnode));
    }

    /**
     * Constructor.
     * @param node Method node.
     */
    private XmlMethod(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Constructor.
     *
     * @param name Method name.
     * @param access Access modifiers.
     * @param descriptor Method descriptor.
     * @param exceptions Method exceptions.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    XmlMethod(
        final String name,
        final int access,
        final String descriptor,
        final String... exceptions
    ) {
        this(
            XmlMethod.prestructor(
                name,
                access,
                descriptor,
                XmlMethod.UNDEFINED,
                XmlMethod.UNDEFINED,
                exceptions
            )
        );
    }

    /**
     * Constructor.
     *
     * @param stack Max stack.
     * @param locals Max locals.
     */
    XmlMethod(final int stack, final int locals) {
        this(XmlMethod.prestructor("foo", Opcodes.ACC_PUBLIC, "()V", stack, locals));
    }

    /**
     * Convert to bytecode.
     * @return Bytecode method.
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

    /**
     * Whether the node is a method.
     * @return True if the node is a method.
     */
    boolean isMethod() {
        return this.node.base().map(XmlMethod.METHOD::equals).orElse(false);
    }

    /**
     * Method attributes.
     * @return Attributes.
     */
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

    /**
     * Method name.
     * @return Name.
     */
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
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException(
                    String.format("Method '%s' doesn't have a name", this.node.name())
                )
            );
    }

    /**
     * All instructions.
     * @return Instructions.
     */
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

    /**
     * Convert to an entry.
     * @param node Node.
     * @return Bytecode entry.
     */
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

    /**
     * Method max stack and locals.
     *
     * @return Maxs.
     */
    private Optional<XmlMaxs> maxs() {
        return this.node.children()
            .map(XmlJeoObject::new)
            .filter(XmlJeoObject::named)
            .filter(object -> object.name().contains("maxs"))
            .findFirst()
            .map(XmlMaxs::new);
    }

    /**
     * Method properties as bytecode.
     *
     * @return Properties.
     */
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

    /**
     * Method access modifiers.
     *
     * @return Access modifiers.
     */
    private int access() {
        return (int) new XmlValue(this.child("access")).object();
    }

    /**
     * Method descriptor.
     *
     * @return Descriptor.
     */
    private String descriptor() {
        return new XmlValue(this.child("descriptor")).string();
    }

    /**
     * Method signature.
     *
     * @return Signature.
     */
    private String signature() {
        return new XmlValue(this.child("signature")).string();
    }

    /**
     * Get child by name.
     * @param name Name.
     * @return Child.
     */
    private XmlNamedObject child(final String name) {
        return this.node.children()
            .map(XmlNamedObject::new)
            .filter(XmlNamedObject::named)
            .filter(object -> object.name().contains(name))
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException(
                    String.format(
                        "Method '%s' doesn't have '%s' child",
                        this.name(),
                        name
                    )
                )
            );
    }

    /**
     * Method trycatch entries.
     *
     * @return Trycatch entries.
     */
    private List<XmlTryCatchEntry> trycatchEntries() {
        return this.node.children()
            .map(XmlSeq::new)
            .filter(XmlSeq::named)
            .filter(seq -> seq.name().contains("trycatchblocks"))
            .flatMap(XmlSeq::children)
            .map(XmlTryCatchEntry::new)
            .collect(Collectors.toList());
    }

    /**
     * Method annotations.
     *
     * @return Annotations.
     */
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

    /**
     * Annotation default value.
     *
     * @return Optional XMIR of the default value.
     */
    private Optional<XmlDefaultValue> defvalue() {
        return this.node.children()
            .map(XmlDefaultValue::new)
            .filter(XmlDefaultValue::isDefaultValue)
            .findFirst();
    }

    /**
     * Method parameters.
     * @return Parameters.
     */
    private BytecodeMethodParameters params() {
        return this.node.children()
            .map(XmlMethodParams::new)
            .filter(XmlMethodParams::isParams)
            .findFirst()
            .map(XmlMethodParams::params)
            .orElse(new BytecodeMethodParameters());
    }

    /**
     * Method exceptions.
     *
     * @return Exceptions.
     */
    private String[] exceptions() {
        return this.node.children()
            .map(XmlExceptions::new)
            .filter(XmlExceptions::isExceptions)
            .findFirst()
            .map(n -> n.bytecode().stream())
            .orElse(Stream.empty())
            .toArray(String[]::new);
    }

    /**
     * Create Method MyXmlNode by directives.
     *
     * @param name Method name.
     * @param access Method access modifiers.
     * @param descriptor Method descriptor.
     * @param stack Max stack.
     * @param locals Max locals.
     * @param exceptions Method exceptions.
     * @return Method MyXmlNode.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    private static XmlNode prestructor(
        final String name,
        final int access,
        final String descriptor,
        final int stack,
        final int locals,
        final String... exceptions
    ) {
        return new NativeXmlNode(
            new Xembler(
                new DirectivesMethod(
                    name,
                    new DirectivesMethodProperties(
                        access,
                        descriptor,
                        "",
                        exceptions,
                        new DirectivesMaxs(new Format(), stack, locals),
                        new DirectivesMethodParams()
                    )
                ),
                new Transformers.Node()
            ).xmlQuietly()
        );
    }
}
