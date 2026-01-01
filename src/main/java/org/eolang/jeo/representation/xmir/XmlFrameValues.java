/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Arrays;
import org.objectweb.asm.Opcodes;

/**
 * Mirror for {@link org.eolang.jeo.representation.directives.DirectivesFrameValues}.
 * This class parses the frame values and their aliases.
 * @since 0.14.0
 */
final class XmlFrameValues {

    /**
     * Xmir node representing frame values.
     */
    private final XmlNode root;

    /**
     * Constructor.
     * @param root Xmir node representing frame values.
     */
    XmlFrameValues(final XmlNode root) {
        this.root = root;
    }

    /**
     * Parse values from the Xmir node.
     * @return Parsed values as an array of objects.
     */
    Object[] values() {
        return Arrays.stream(new XmlValues(this.root).values())
            .map(XmlFrameValues::parse)
            .toArray();
    }

    /**
     * Parse a single value from the Xmir node.
     * This method was added to simplify the XMIR representation of bytecode frames.
     * <p>
     *     You can read more about the original intention right here:
     *     <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1211">Issue</a>.
     * </p>
     * @param value Value to parse, can be a string or an integer.
     * @return Parsed value as an object, which can be an alias or the original value.
     * @checkstyle CyclomaticComplexityCheck (50 lines)
     */
    private static Object parse(final Object value) {
        final Object result;
        if (value instanceof String) {
            switch ((String) value) {
                case "top":
                    result = Opcodes.TOP;
                    break;
                case "short":
                case "boolean":
                case "char":
                case "byte":
                case "integer":
                    result = Opcodes.INTEGER;
                    break;
                case "float":
                    result = Opcodes.FLOAT;
                    break;
                case "double":
                    result = Opcodes.DOUBLE;
                    break;
                case "long":
                    result = Opcodes.LONG;
                    break;
                case "null":
                    result = Opcodes.NULL;
                    break;
                case "uninit_this":
                    result = Opcodes.UNINITIALIZED_THIS;
                    break;
                default:
                    result = value;
                    break;
            }
        } else {
            result = value;
        }
        return result;
    }
}
