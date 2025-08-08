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
import org.eolang.jeo.representation.directives.JeoFqn;

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
     * Long full qualified name.
     */
    private static final String LONG = new JeoFqn("long").fqn();

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
     */
    public Object object() {
        final String base = XmlValue.base(this.node);
        final Object res;
        if (XmlValue.TRUE.equals(base) || XmlValue.FALSE.equals(base)) {
            res = new XmlClosedObject(this.node)
                .optbase()
                .map(XmlValue.TRUE::equals)
                .orElse(false);
        } else if (XmlValue.NUMBER.equals(base)) {
            res = new BytecodeBytes(XmlValue.withoutPackage(base), this.bytes())
                .object(new EoCodec());
        } else if (XmlValue.LONG.equals(base)) {
            final XmlNode child = new XmlJeoObject(this.node)
                .children()
                .findFirst()
                .orElseThrow(
                    () -> new IllegalStateException(
                        String.format("Can't find a child in '%s' to convert to long", this.node)
                    )
                );
            final boolean nonumber = !(XmlValue.NUMBER.equals(XmlValue.base(child)));
            Codec codec = new EoCodec();
            if (nonumber) {
                codec = new PlainLongCodec(codec);
            }
            res = new BytecodeBytes(XmlValue.withoutPackage(base), this.bytes()).object(codec);
        } else {
            res = new BytecodeBytes(XmlValue.withoutPackage(base), this.bytes())
                .object(new EoCodec());
        }
        return res;
    }

    /**
     * Get the last part of the base without the package.
     * @param base Base of the object, e.g. "org.eolang.jeo.String".
     * @return Last part of the base, e.g. "String".
     */
    private static String withoutPackage(final String base) {
        final String result;
        final int last = base.lastIndexOf('.');
        if (last == -1) {
            result = base;
        } else {
            result = base.substring(last + 1);
        }
        return result;
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
        return new XmlDelegateObject(node)
            .base()
            .orElseGet(() -> new XmlClosedObject(node).base());
    }
}
