/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.regex.Pattern;
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
        final String base = this.base();
        final Object res;
        if ("bool".equals(base)) {
            res = this.parseBoolean();
        } else {
            Codec codec = new EoCodec();
            if (!this.node.child("o").hasAttribute("base", new EoFqn("number").fqn())) {
                codec = new PlainLongCodec(codec);
            }
            res = new BytecodeBytes(base, this.bytes()).object(codec);
        }
        return res;
    }

    /**
     * Parse boolean value.
     * @return Boolean.
     */
    private Object parseBoolean() {
        return this.node.child("o").hasAttribute("base", new EoFqn("true").fqn());
    }

    /**
     * Convert hex string to a byte array.
     * @return Byte array.
     */
    private byte[] bytes() {
        final String hex = this.hex();
        final byte[] res;
        if (hex.isEmpty()) {
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
        return XmlValue.DELIMITER.matcher(this.node.firstChild().text().trim()).replaceAll("");
    }

    /**
     * Get the type of the object without a package.
     * @return Type without package.
     */
    private String base() {
        final String base = this.node.attribute("base")
            .orElseThrow(
                () -> new IllegalStateException(
                    String.format(
                        "'%s' is not an argument because it doesn't have 'base' attribute",
                        this.node
                    )
                )
            );
        final String result;
        final int last = base.lastIndexOf('.');
        if (last == -1) {
            result = base;
        } else {
            result = base.substring(last + 1);
        }
        return result;
    }
}
