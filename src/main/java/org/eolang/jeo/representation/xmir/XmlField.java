/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Optional;
import org.eolang.jeo.representation.PrefixedName;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeField;
import org.eolang.jeo.representation.directives.JeoFqn;

/**
 * XML field.
 *
 * @since 0.1
 */
public final class XmlField {

    /**
     * Field base full qualified name.
     */
    private static final String FIELD = new JeoFqn("field").fqn();

    /**
     * Field node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     *
     * @param xmlnode Field node
     */
    XmlField(final XmlNode xmlnode) {
        this(new XmlJeoObject(xmlnode));
    }

    /**
     * Constructor.
     *
     * @param node XML Jeo object node
     */
    private XmlField(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Convert to bytecode.
     *
     * @return Bytecode field
     */
    public BytecodeField bytecode() {
        return new BytecodeField(
            this.name(),
            this.descriptor(),
            this.signature(),
            this.value(),
            this.access(),
            this.annotations().map(XmlAnnotations::bytecode).orElse(new BytecodeAnnotations())
        );
    }

    @Override
    public String toString() {
        return String.format("XmlField(node=%s)", this.node);
    }

    /**
     * Whether this node is a field.
     *
     * @return True if this node is a field, false otherwise
     */
    boolean isField() {
        return this.node.base().map(XmlField.FIELD::equals).orElse(false);
    }

    private String name() {
        return new PrefixedName(this.node.name()).decode();
    }

    private int access() {
        return this.find(Attribute.ACCESS).map(XmlValue::object).map(Integer.class::cast).orElse(0);
    }

    private String descriptor() {
        return this.find(Attribute.DESCRIPTOR).map(XmlValue::string)
            .filter(descriptor -> !descriptor.isEmpty())
            .orElse(null);
    }

    private String signature() {
        return this.find(Attribute.SIGNATURE).map(XmlValue::string)
            .filter(signature -> !signature.isEmpty())
            .orElse(null);
    }

    private Object value() {
        return new XmlOperand(
            this.child(Attribute.VALUE).orElseThrow(
                () -> new IllegalStateException(
                    String.format("Field '%s' doesn't have a 'value' attribute", this.name())
                )
            )
        ).asObject();
    }

    private Optional<XmlAnnotations> annotations() {
        final String name = String.format("annotations-%s", this.name());
        return this.node.children()
            .map(XmlJeoObject::new)
            .filter(XmlJeoObject::named)
            .filter(object -> name.equals(object.name()))
            .findFirst()
            .map(XmlAnnotations::new);
    }

    private Optional<XmlValue> find(final Attribute attribute) {
        return this.child(attribute).map(XmlValue::new);
    }

    private Optional<XmlNode> child(final Attribute attribute) {
        return this.node.children()
            .map(XmlNamedObject::new)
            .filter(XmlNamedObject::named)
            .filter(object -> attribute.title().equals(object.name()))
            .map(XmlNamedObject::node)
            .findFirst();
    }

    /**
     * Field attribute.
     * Each attribute is found by its name in the XML representation.
     *
     * @since 0.1
     */
    private enum Attribute {

        /**
         * Access modifier.
         * It's a number that represents sum of access modifiers.
         * For example, if the field is public and static, then the value will be 9.
         * See {@link org.objectweb.asm.Opcodes} for more details.
         */
        ACCESS("access"),

        /**
         * Field name.
         */
        NAME("name"),

        /**
         * Field descriptor.
         * For example, for field of type int the descriptor will be "I".
         */
        DESCRIPTOR("descriptor"),

        /**
         * Field signature.
         * The field signature is used for defining generic types.
         * Signature grammar:
         *
         * <p>{@code TypeSignature: Z|C|B|S|I|F|J|D|FieldTypeSignature FieldTypeSignature:
         * ClassTypeSignature | [ TypeSignature | TypeVar ClassTypeSignature: L Id
         * ( / Id )* TypeArgs? ( . Id TypeArgs? )* ; TypeArgs: < TypeArg+ > TypeArg: * |
         * ( + | - )? FieldTypeSignature TypeVar: T Id ; }</p>
         * Examples:
         *
         * <p>{@code List<E> -> Ljava/util/List<TE;>; List<?> -> Ljava/util/List<*>; List<? extends
         * Number> -> Ljava/util/List<+Ljava/lang/Number;>; List<? super Integer> ->
         * Ljava/util/List<-Ljava/lang/Integer;>; List<List<String>[]> ->
         * Ljava/util/List<[Ljava/util/List<Ljava/lang/String;>;>; HashMap<K, V>.HashIterator<K>
         * -> Ljava/util/HashMap<TK;TV;>.HashIterator<TK;>; }</p>
         * You can read more in 4.1.1 section of the ASM
         * <a href="https://asm.ow2.io/asm4-guide.pdf">manual </a>
         */
        SIGNATURE("signature"),

        /**
         * Initial field value.
         * For example, for field of type int with value 19, the value will be "13".
         * Any data type will be represented as a hex string.
         * May not be set.
         */
        VALUE("value");

        /**
         * Name of the attribute in the XML representation.
         */
        private final String label;

        /**
         * Constructor.
         *
         * @param label Name of the attribute in the XML representation
         */
        Attribute(final String label) {
            this.label = label;
        }

        private String title() {
            return this.label;
        }
    }
}
