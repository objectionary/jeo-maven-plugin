/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.eolang.jeo.representation.bytecode.BytecodeBytes;
import org.eolang.jeo.representation.bytecode.Codec;
import org.eolang.jeo.representation.bytecode.EoCodec;
import org.eolang.jeo.representation.bytecode.PlainLongCodec;
import org.eolang.jeo.representation.directives.EoFqn;

/**
 * XML value.
 * @since 0.6
 */
public final class XmlValue {

    /**
     * Hex radix.
     */
    private static final int RADIX = 16;

    /**
     * Boolean TRUE full qualified name.
     */
    private static final String TRUE = new EoFqn("true").fqn();

    /**
     * Boolean FALSE full qualified name.
     */
    private static final String FALSE = new EoFqn("false").fqn();

    /**
     * Number full qualified name.
     */
    private static final String NUMBER = new EoFqn("number").fqn();

    /**
     * Space pattern.
     */
    private static final Pattern DELIMITER = Pattern.compile("-");

    /**
     * XML node.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param node XML node.
     */
    public XmlValue(final XmlNamedObject node) {
        this(node.node());
    }

    /**
     * Constructor.
     * @param node XML node.
     */
    public XmlValue(final XmlNode node) {
        this.node = node;
    }

    /**
     * Convert hex string to human-readable string.
     * Example:
     * {@code
     *  "48 65 6C 6C 6F 20 57 6F 72 6C 64 21" -> "Hello World!"
     *  }
     * @return Human-readable string.
     */
    public String string() {
        return (String) this.object();
    }

    /**
     * Convert hex string to an object.
     * @return Object.
     * @todo #1130:90min Cover {@link XmlValue#object()} with tests.
     *  Currently, this method is overly complex and not covered by tests.
     *  Actually, this code caused several issues in integration tests which
     *  were not caught by unit tests.
     */
    public Object object() {
        final String base = XmlValue.base(this.node);
        final Object res;
        if (XmlValue.isBoolean(base)) {
            res = new XmlClosedObject(this.node).optbase()
                .map(XmlValue.TRUE::equals)
                .orElse(false);
        } else {
            Codec codec = new EoCodec();
            final XmlNode child;
            final boolean based;
            if (new XmlJeoObject(this.node).isJeoObject()) {
                based = true;
                child = new XmlJeoObject(this.node)
                    .children()
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);
            } else {
                child = this.node.child("o");
                based = child.attribute("base").isPresent();
            }
            final boolean nonumber = !(based && XmlValue.NUMBER.equals(XmlValue.base(child)));
            if (nonumber) {
                codec = new PlainLongCodec(codec);
            }
            final String result;
            final int last = base.lastIndexOf('.');
            if (last == -1) {
                result = base;
            } else {
                result = base.substring(last + 1);
            }
            res = new BytecodeBytes(result, this.bytes()).object(codec);
        }
        return res;
    }

    /**
     * Object base.
     * @param base Object 'base' attribute value.
     * @return True if it's boolean, false otherwise.
     */
    private static boolean isBoolean(final String base) {
        return XmlValue.TRUE.equals(base) || XmlValue.FALSE.equals(base);
    }

    /**
     * Convert hex string to a byte array.
     * @return Byte array.
     */
    private byte[] bytes() {
        final String hex = this.hex();
        final byte[] res;
        if (hex.isEmpty() || "--".equals(hex)) {
            res = null;
        } else {
            final char[] chars = hex.toCharArray();
            final int length = chars.length;
            res = new byte[length / 2];
            for (int index = 0; index < length; index += 2) {
                res[index / 2] = (byte) Integer.parseInt(
                    String.copyValueOf(new char[]{chars[index], chars[index + 1]}), XmlValue.RADIX
                );
            }
        }
        return res;
    }

    /**
     * Hex string.
     * Example:
     * - "20 57 6F 72 6C 64 21" -> "20576F726C6421"
     * @return Hex string.
     */
    private String hex() {
        final XmlJeoObject object = new XmlJeoObject(this.node);
        final Stream<XmlNode> children;
        if (object.isJeoObject()) {
            children = object.children();
        } else {
            children = this.node.children();
        }
        return XmlValue.DELIMITER.matcher(
            children
                .findFirst()
                .orElseThrow(
                    () -> new IllegalStateException(
                        String.format(
                            "Can't find a child in '%s' to convert to hex",
                            this.node
                        )
                    )
                )
                .text().trim()
        ).replaceAll("");
    }

    /**
     * Get the type of the object without a package.
     * @param node XML node of the object to get the base from.
     * @return Type without package.
     */
    private static String base(final XmlNode node) {
        return new XmlJeoObject(node)
            .base()
            .orElseGet(() -> new XmlClosedObject(node).base());
    }
}
