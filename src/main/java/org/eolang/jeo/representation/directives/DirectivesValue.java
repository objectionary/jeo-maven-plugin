/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import lombok.ToString;
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
@ToString
@SuppressWarnings("PMD.TooManyMethods")
public final class DirectivesValue implements Iterable<Directive> {

    /**
     * Array of hexadecimal characters.
     * Used for converting bytes to hexadecimal.
     * See {@link #bytesToHex(byte[])}.
     */
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     * Maximum long value that can be represented as double.
     * Any value greater than this will be represented incorrectly.
     */
    private static final long MAX_LONG_DOUBLE = 9_007_199_254_740_992L;

    /**
     * Minimum long value that can be represented as double.
     * Any value less than this will be represented incorrectly.
     */
    private static final long MIN_LONG_DOUBLE = -9_007_199_254_740_992L;

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
     * @param data Data.
     * @param <T> Data type.
     */
    public <T> DirectivesValue(final T data) {
        this(0, new Format(), data);
    }

    /**
     * Constructor.
     *
     * @param index Ordered index.
     * @param format Directives format.
     * @param data Data.
     * @param <T> Data type.
     */
    public <T> DirectivesValue(final int index, final Format format, final T data) {
        this(format, new NumName("v", index), data);
    }

    /**
     * Constructor.
     *
     * @param format Directives format.
     * @param name Name.
     * @param data Data.
     * @param <T> Data type.
     */
    public <T> DirectivesValue(final Format format, final NumName name, final T data) {
        this(format, name.toString(), new BytecodeValue(data));
    }

    /**
     * Constructor.
     *
     * @param format Directives format.
     * @param name Name.
     * @param data Data.
     * @param <T> Data type.
     */
    public <T> DirectivesValue(final Format format, final String name, final T data) {
        this(format, name, new BytecodeValue(data));
    }

    /**
     * Constructor.
     *
     * @param format Directives format.
     * @param name Name.
     * @param as The 'as' attribute of the object.
     * @param data Data.
     * @param <T> Data type.
     * @checkstyle ParameterNumberCheck (5 lines)
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
     * @param format Format.
     * @param name Name.
     * @param value Value.
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
     * @param format Format.
     * @param name Name.
     * @param as The 'as' attribute of the object.
     * @param value Value.
     * @checkstyle ParameterNumberCheck (5 lines)
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
     * @return Iterator of directives.
     * @checkstyle CyclomaticComplexityCheck (50 lines)
     * @checkstyle NoJavadocForOverriddenMethodsCheck (50 lines)
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

    /**
     * Type of the data.
     *
     * @return Type
     */
    String type() {
        return this.value.type();
    }

    /**
     * Check if the value fits into the double.
     *
     * @return True if fits.
     */
    private boolean fits() {
        final long val = ((Number) this.value.value()).longValue();
        return val >= DirectivesValue.MIN_LONG_DOUBLE && val <= DirectivesValue.MAX_LONG_DOUBLE;
    }

    /**
     * EO object.
     *
     * @param base Base.
     * @param codec Codec to use for bytes encoding.
     * @return EO object directives.
     */
    private DirectivesEoObject eoObject(final String base, final Codec codec) {
        return new DirectivesEoObject(
            base,
            this.name,
            this.as,
            new DirectivesComment(this.format, this.comment()),
            new DirectivesBytes(this.hex(codec), "", "α0")
        );
    }

    /**
     * Nullable object.
     * @param base Base of the object, usually "nullable".
     * @param codec Codec to use for bytes encoding.
     * @return JEO object directives for nullable type.
     */
    private DirectivesJeoObject nullable(final String base, final Codec codec) {
        return new DirectivesJeoObject(
            base,
            this.name,
            new DirectivesComment(this.format, this.comment()),
            new DirectivesBytes(this.hex(codec), new NumName("n", 0).toString())
        );
    }

    /**
     * JEO object.
     *
     * @param base Base.
     * @param codec Codec to use for bytes encoding.
     * @return JEO object directives.
     */
    private DirectivesJeoObject jeoObject(final String base, final Codec codec) {
        return new DirectivesJeoObject(
            base,
            this.name,
            new DirectivesComment(this.format, this.comment()),
            new DirectivesBytes(this.hex(codec), new NumName("j", 0).toString())
        );
    }

    /**
     * JEO number.
     *
     * @param base Object base.
     * @param codec Codec to use for bytes encoding.
     * @return JEO number directives.
     */
    private DirectivesJeoObject jeoNumber(final String base, final Codec codec) {
        return new DirectivesJeoObject(
            base,
            this.name,
            this.as,
            new Directives(new DirectivesComment(this.format, this.comment())),
            new Directives(new DirectivesNumber(new NumName("n", 0).toString(), this.hex(codec)))
        );
    }

    /**
     * Integer number object.
     * We decided to use simplified representation of integer numbers.
     * Previously, we used {@link #jeoNumber(String, Codec)} wrapper.
     * You can read about it here:
     * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1061">#1061</a>
     * @param codec Codec to use for bytes encoding.
     * @return JEO number directives.
     */
    private Iterable<Directive> integerNumber(final Codec codec) {
        return new DirectivesNumber(this.name, this.hex(codec));
    }

    /**
     * Boolean object.
     *
     * @return Boolean object directives.
     */
    private Iterable<Directive> booleanObject() {
        final String base;
        if ((boolean) this.value.value()) {
            base = "true";
        } else {
            base = "false";
        }
        return new DirectivesEoObject(base, this.name);
    }

    /**
     * Bytes of the representative comment.
     *
     * @return Sting comment.
     */
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

    /**
     * Bytes to HEX.
     * The efficient way to convert bytes to hexadecimal.
     * ATTENTION!
     * Do not modify this method.
     * It is an optimized version that saves memory and CPU.
     * Actually, the solution is based on the following StackOverflow answer:
     * <a href="https://stackoverflow.com/a/9855338/10423604">here</a>
     * You can find the full explanation or any other examples there.
     *
     * @param bytes Bytes.
     * @return Hexadecimal value as string.
     */
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
