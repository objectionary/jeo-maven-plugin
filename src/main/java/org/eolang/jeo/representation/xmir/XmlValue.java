/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
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
 *
 * @since 0.6
 */
public final class XmlValue {

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
     *
     * @param node XML node
     */
    public XmlValue(final XmlNamedObject node) {
        this(node.node());
    }

    /**
     * Constructor.
     *
     * @param node XML node
     */
    public XmlValue(final XmlNode node) {
        this.node = node;
    }

    /**
     * Convert hex string to human-readable string.
     * Example:
     * {@code
     * "48 65 6C 6C 6F 20 57 6F 72 6C 64 21" -> "Hello World!"
     * }
     *
     * @return Human-readable string
     */
    public String string() {
        return (String) this.object();
    }

    /**
     * Convert hex string to an object.
     *
     * @return Object
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
            final boolean nonumber = !XmlValue.NUMBER.equals(
                XmlValue.base(
                    new XmlJeoObject(this.node)
                        .children()
                        .findFirst().orElseThrow(
                            () -> new IllegalStateException(
                                String.format(
                                    "Can't find a child in '%s' to convert to long", this.node
                                )
                            )
                        )
                )
            );
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

    private byte[] bytes() {
        final String hex = this.hex();
        final byte[] res;
        if (hex.isEmpty() || "--".equals(hex)) {
            res = null;
        } else {
            final char[] chars = hex.toCharArray();
            final int length = chars.length;
            if (length % 2 != 0) {
                throw new IllegalStateException(
                    String.format(
                        "The value '%s' of '%s' has an odd number of hex digits",
                        hex, this.node
                    )
                );
            }
            res = new byte[length / 2];
            for (int index = 0; index < length; index += 2) {
                final String pair = String.copyValueOf(
                    new char[]{chars[index], chars[index + 1]}
                );
                try {
                    res[index / 2] = (byte) Integer.parseInt(pair, 16);
                } catch (final NumberFormatException exception) {
                    throw new IllegalStateException(
                        String.format(
                            "The pair '%s' in the value '%s' of '%s' is not hex",
                            pair, hex, this.node
                        ),
                        exception
                    );
                }
            }
        }
        return res;
    }

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
                .findFirst().orElseThrow(
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

    private static String base(final XmlNode node) {
        return new XmlDelegateObject(node)
            .base()
            .orElseGet(() -> new XmlClosedObject(node).base());
    }
}
