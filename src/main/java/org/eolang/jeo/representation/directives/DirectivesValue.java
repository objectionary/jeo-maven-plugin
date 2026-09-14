/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.eolang.jeo.representation.bytecode.BytecodeValue;
import org.eolang.jeo.representation.bytecode.Codec;
import org.eolang.jeo.representation.bytecode.EoCodec;
import org.eolang.jeo.representation.bytecode.PlainLongCodec;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Data Object Directive in EO language.
 *
 * @since 0.1.0
 */
public final class DirectivesValue implements Iterable<Directive> {

    /**
     * Array of hexadecimal characters.
     * Used for converting bytes to hexadecimal.
     * See {@link #bytesToHex(byte[])}.
     */
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     * Default codec.
     */
    private static final Codec CODEC = new EoCodec();

    /**
     * Directives format.
     */
    private final Format format;

    /**
     * Name.
     */
    private final String name;

    /**
     * The 'as' attribute of the object.
     *
     * @checkstyle MemberNameCheck (2 lines)
     */
    private final String as;

    /**
     * Value.
     */
    private final BytecodeValue value;

    /**
     * Constructor.
     *
     * @param data Data
     * @param <T> Data type
     */
    public <T> DirectivesValue(final T data) {
        this(0, new Format(), data);
    }

    /**
     * Constructor.
     *
     * @param index Ordered index
     * @param format Directives format
     * @param data Data
     * @param <T> Data type
     */
    public <T> DirectivesValue(final int index, final Format format, final T data) {
        this(format, new NumName("v", index), data);
    }

    /**
     * Constructor.
     *
     * @param format Directives format
     * @param name Name
     * @param data Data
     * @param <T> Data type
     */
    public <T> DirectivesValue(final Format format, final NumName name, final T data) {
        this(format, name.toString(), new BytecodeValue(data));
    }

    /**
     * Constructor.
     *
     * @param format Directives format
     * @param name Name
     * @param data Data
     * @param <T> Data type
     */
    public <T> DirectivesValue(final Format format, final String name, final T data) {
        this(format, name, new BytecodeValue(data));
    }

    /**
     * Constructor.
     *
     * @param format Directives format
     * @param name Name
     * @param as The 'as' attribute of the object
     * @param data Data
     * @param <T> Data type
     * @checkstyle ParameterNameCheck (5 lines)
     */
    public <T> DirectivesValue(
        final Format format,
        final String name,
        final String as,
        final T data
    ) {
        this(format, name, as, new BytecodeValue(data));
    }

    /**
     * Constructor.
     *
     * @param format Format
     * @param name Name
     * @param value Value
     */
    public DirectivesValue(
        final Format format,
        final String name,
        final BytecodeValue value
    ) {
        this(format, name, "", value);
    }

    /**
     * Constructor.
     *
     * @param format Format
     * @param name Name
     * @param as The 'as' attribute of the object
     * @param value Value
     * @checkstyle ParameterNameCheck (5 lines)
     */
    public DirectivesValue(
        final Format format,
        final String name,
        final String as,
        final BytecodeValue value
    ) {
        this.format = format;
        this.name = name;
        this.as = as;
        this.value = value;
    }

    /**
     * Iterator of directives.
     *
     * @return Iterator of directives
     * @checkstyle CyclomaticComplexityCheck (50 lines)
     * @checkstyle NoJavadocForOverriddenMethodsCheck (50 lines)
     * @checkstyle MissingNullCaseInSwitchCheck (50 lines)
     */
    @Override
    public Iterator<Directive> iterator() {
        final String type = this.type();
        final Iterable<Directive> res;
        final Codec codec = DirectivesValue.CODEC;
        switch (type) {
            case "number":
                res = this.integerNumber(codec);
                break;
            case "byte":
            case "short":
            case "float":
            case "double":
                res = this.jeoNumber(type, codec);
                break;
            case "long":
                if (this.fits()) {
                    res = this.jeoNumber(type, codec);
                } else {
                    res = this.jeoObject(type, new PlainLongCodec(codec));
                }
                break;
            case "nullable":
                res = this.nullable(type, codec);
                break;
            case "string":
                res = this.eoObject(type, codec);
                break;
            case "bool":
                res = this.booleanObject();
                break;
            default:
                res = this.jeoObject(type, codec);
                break;
        }
        return res.iterator();
    }

    /**
     * Value of the data.
     *
     * @param codec Codec
     * @return Value
     */
    public String hex(final Codec codec) {
        return DirectivesValue.bytesToHex(this.value.encode(codec));
    }

    @Override
    public String toString() {
        return String.format(
            "DirectivesValue(format=%s, name=%s, as=%s, value=%s)",
            this.format, this.name, this.as, this.value
        );
    }

    /**
     * Type of the data.
     *
     * @return Type
     */
    String type() {
        return this.value.type();
    }

    // Beyond +/-2^53, a long cannot be represented as a double without losing precision.
    private boolean fits() {
        final long val = ((Number) this.value.value()).longValue();
        return val >= -9_007_199_254_740_992L && val <= 9_007_199_254_740_992L;
    }

    private DirectivesEoObject eoObject(final String base, final Codec codec) {
        return new DirectivesEoObject(
            base,
            this.name,
            this.as,
            new DirectivesComment(this.format, this.comment()),
            new DirectivesBytes(this.hex(codec), "", "as-bytes")
        );
    }

    private DirectivesJeoObject nullable(final String base, final Codec codec) {
        return new DirectivesJeoObject(
            base,
            this.name,
            new DirectivesComment(this.format, this.comment()),
            new DirectivesBytes(this.hex(codec), new NumName("n", 0).toString())
        );
    }

    private DirectivesJeoObject jeoObject(final String base, final Codec codec) {
        return new DirectivesJeoObject(
            base,
            this.name,
            new DirectivesComment(this.format, this.comment()),
            new DirectivesBytes(this.hex(codec), new NumName("j", 0).toString())
        );
    }

    private DirectivesJeoObject jeoNumber(final String base, final Codec codec) {
        return new DirectivesJeoObject(
            base,
            this.name,
            this.as,
            new Directives(new DirectivesComment(this.format, this.comment())),
            new Directives(new DirectivesNumber(new NumName("n", 0).toString(), this.hex(codec)))
        );
    }

    private Iterable<Directive> integerNumber(final Codec codec) {
        return new DirectivesNumber(this.name, this.hex(codec));
    }

    private Iterable<Directive> booleanObject() {
        final String base;
        if ((boolean) this.value.value()) {
            base = "true";
        } else {
            base = "false";
        }
        return new DirectivesEoObject(base, this.name);
    }

    private String comment() {
        final String result;
        final Object object = this.value.value();
        if (object instanceof String) {
            result = String.format("\"%s\"", object);
        } else {
            result = String.valueOf(object);
        }
        return result;
    }

    private static String bytesToHex(final byte[] bytes) {
        final String res;
        if (bytes == null || bytes.length == 0) {
            res = "--";
        } else {
            final int length = bytes.length;
            final char[] hex = new char[length * 3];
            for (int index = 0; index < length; ++index) {
                final int value = bytes[index] & 0xFF;
                hex[index * 3] = DirectivesValue.HEX_ARRAY[value >>> 4];
                hex[index * 3 + 1] = DirectivesValue.HEX_ARRAY[value & 0x0F];
                hex[index * 3 + 2] = '-';
            }
            if (hex.length == 3) {
                res = new String(hex);
            } else {
                res = new String(hex, 0, hex.length - 1);
            }
        }
        return res;
    }
}
