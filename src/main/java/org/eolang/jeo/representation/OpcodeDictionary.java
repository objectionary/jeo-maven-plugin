/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Opcode dictionary.
 * This class provides a mapping between opcode numbers and their corresponding names,
 * allowing for easy retrieval of opcode names by their numbers and vice versa.
 * @since 0.12.0
 */
public final class OpcodeDictionary {

    /**
     * Default opcode names and codes.
     */
    private static final Map<Integer, String> NAMES = OpcodeDictionary.opcodeNames();

    /**
     * Default opcode codes.
     */
    private static final Map<String, Integer> CODES = OpcodeDictionary.nameOpcodes();

    /**
     * Dictionary of opcode names.
     */
    private final Map<Integer, String> names;

    /**
     * Dictionary of opcode codes.
     */
    private final Map<String, Integer> codes;

    /**
     * Constructor for creating an instance with default opcode names and codes.
     */
    public OpcodeDictionary() {
        this(OpcodeDictionary.NAMES, OpcodeDictionary.CODES);
    }

    /**
     * Constructor for creating an instance with custom maps.
     * @param names Map of opcode numbers to their names
     * @param codes Map of opcode names to their corresponding numbers
     */
    private OpcodeDictionary(final Map<Integer, String> names, final Map<String, Integer> codes) {
        this.names = names;
        this.codes = codes;
    }

    /**
     * Retrieves the opcode name for a given opcode number.
     * @param opcode Opcode number
     * @return Name of the opcode
     */
    public String name(final int opcode) {
        final String result;
        if (this.names.containsKey(opcode)) {
            result = this.names.get(opcode);
        } else {
            throw new IllegalStateException(
                String.format("Unknown opcode: %d", opcode)
            );
        }
        return result;
    }

    /**
     * Retrieves the opcode name for a given opcode number.
     * @param name Name of the opcode
     * @return Opcode number corresponding to the name
     */
    public int code(final String name) {
        final int result;
        final String key = name.toLowerCase(Locale.ROOT);
        if (this.codes.containsKey(key)) {
            result = this.codes.get(key);
        } else {
            throw new IllegalStateException(String.format("Unknown opcode name: %s", name));
        }
        return result;
    }

    /**
     * Returns a map of opcode numbers to their corresponding names.
     * @return Map of opcode numbers to names
     */
    private static Map<Integer, String> opcodeNames() {
        return Collections.unmodifiableMap(
            OpcodeDictionary.entries().collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (old, fresh) -> fresh
                )
            )
        );
    }

    /**
     * Returns a map of opcode names to their corresponding integer values.
     * @return Map of opcode names to their integer values
     */
    private static Map<String, Integer> nameOpcodes() {
        return Collections.unmodifiableMap(
            OpcodeDictionary.entries().collect(
                Collectors.toMap(
                    Map.Entry::getValue,
                    Map.Entry::getKey,
                    (old, fresh) -> fresh
                )
            )
        );
    }

    /**
     * Entries of the opcode dictionary.
     * @return Stream of opcode entries
     * @checkstyle MethodLengthCheck (200 lines)
     */
    private static Stream<Map.Entry<Integer, String>> entries() {
        return Stream.of(
            new Pair(0, "nop"),
            new Pair(1, "aconst_null"),
            new Pair(2, "iconst_m1"),
            new Pair(3, "iconst_0"),
            new Pair(4, "iconst_1"),
            new Pair(5, "iconst_2"),
            new Pair(6, "iconst_3"),
            new Pair(7, "iconst_4"),
            new Pair(8, "iconst_5"),
            new Pair(9, "lconst_0"),
            new Pair(10, "lconst_1"),
            new Pair(11, "fconst_0"),
            new Pair(12, "fconst_1"),
            new Pair(13, "fconst_2"),
            new Pair(14, "dconst_0"),
            new Pair(15, "dconst_1"),
            new Pair(16, "bipush"),
            new Pair(17, "sipush"),
            new Pair(18, "ldc"),
            new Pair(21, "iload"),
            new Pair(22, "lload"),
            new Pair(23, "fload"),
            new Pair(24, "dload"),
            new Pair(25, "aload"),
            new Pair(46, "iaload"),
            new Pair(47, "laload"),
            new Pair(48, "faload"),
            new Pair(49, "daload"),
            new Pair(50, "aaload"),
            new Pair(51, "baload"),
            new Pair(52, "caload"),
            new Pair(53, "saload"),
            new Pair(54, "istore"),
            new Pair(55, "lstore"),
            new Pair(56, "fstore"),
            new Pair(57, "dstore"),
            new Pair(58, "astore"),
            new Pair(79, "iastore"),
            new Pair(80, "lastore"),
            new Pair(81, "fastore"),
            new Pair(82, "dastore"),
            new Pair(83, "aastore"),
            new Pair(84, "bastore"),
            new Pair(85, "castore"),
            new Pair(86, "sastore"),
            new Pair(87, "pop"),
            new Pair(88, "pop2"),
            new Pair(89, "dup"),
            new Pair(90, "dup_x1"),
            new Pair(91, "dup_x2"),
            new Pair(92, "dup2"),
            new Pair(93, "dup2_x1"),
            new Pair(94, "dup2_x2"),
            new Pair(95, "swap"),
            new Pair(96, "iadd"),
            new Pair(97, "ladd"),
            new Pair(98, "fadd"),
            new Pair(99, "dadd"),
            new Pair(100, "isub"),
            new Pair(101, "lsub"),
            new Pair(102, "fsub"),
            new Pair(103, "dsub"),
            new Pair(104, "imul"),
            new Pair(105, "lmul"),
            new Pair(106, "fmul"),
            new Pair(107, "dmul"),
            new Pair(108, "idiv"),
            new Pair(109, "ldiv"),
            new Pair(110, "fdiv"),
            new Pair(111, "ddiv"),
            new Pair(112, "irem"),
            new Pair(113, "lrem"),
            new Pair(114, "frem"),
            new Pair(115, "drem"),
            new Pair(116, "ineg"),
            new Pair(117, "lneg"),
            new Pair(118, "fneg"),
            new Pair(119, "dneg"),
            new Pair(120, "ishl"),
            new Pair(121, "lshl"),
            new Pair(122, "ishr"),
            new Pair(123, "lshr"),
            new Pair(124, "iushr"),
            new Pair(125, "lushr"),
            new Pair(126, "iand"),
            new Pair(127, "land"),
            new Pair(128, "ior"),
            new Pair(129, "lor"),
            new Pair(130, "ixor"),
            new Pair(131, "lxor"),
            new Pair(132, "iinc"),
            new Pair(133, "i2l"),
            new Pair(134, "i2f"),
            new Pair(135, "i2d"),
            new Pair(136, "l2i"),
            new Pair(137, "l2f"),
            new Pair(138, "l2d"),
            new Pair(139, "f2i"),
            new Pair(140, "f2l"),
            new Pair(141, "f2d"),
            new Pair(142, "d2i"),
            new Pair(143, "d2l"),
            new Pair(144, "d2f"),
            new Pair(145, "i2b"),
            new Pair(146, "i2c"),
            new Pair(147, "i2s"),
            new Pair(148, "lcmp"),
            new Pair(149, "fcmpl"),
            new Pair(150, "fcmpg"),
            new Pair(151, "dcmpl"),
            new Pair(152, "dcmpg"),
            new Pair(153, "ifeq"),
            new Pair(154, "ifne"),
            new Pair(155, "iflt"),
            new Pair(156, "ifge"),
            new Pair(157, "ifgt"),
            new Pair(158, "ifle"),
            new Pair(159, "if_icmpeq"),
            new Pair(160, "if_icmpne"),
            new Pair(161, "if_icmplt"),
            new Pair(162, "if_icmpge"),
            new Pair(163, "if_icmpgt"),
            new Pair(164, "if_icmple"),
            new Pair(165, "if_acmpeq"),
            new Pair(166, "if_acmpne"),
            new Pair(167, "goto"),
            new Pair(168, "jsr"),
            new Pair(169, "ret"),
            new Pair(170, "tableswitch"),
            new Pair(171, "lookupswitch"),
            new Pair(172, "ireturn"),
            new Pair(173, "lreturn"),
            new Pair(174, "freturn"),
            new Pair(175, "dreturn"),
            new Pair(176, "areturn"),
            new Pair(177, "return"),
            new Pair(178, "getstatic"),
            new Pair(179, "putstatic"),
            new Pair(180, "getfield"),
            new Pair(181, "putfield"),
            new Pair(182, "invokevirtual"),
            new Pair(183, "invokespecial"),
            new Pair(184, "invokestatic"),
            new Pair(185, "invokeinterface"),
            new Pair(186, "invokedynamic"),
            new Pair(187, "new"),
            new Pair(188, "newarray"),
            new Pair(189, "anewarray"),
            new Pair(190, "arraylength"),
            new Pair(191, "athrow"),
            new Pair(192, "checkcast"),
            new Pair(193, "instanceof"),
            new Pair(194, "monitorenter"),
            new Pair(195, "monitorexit"),
            new Pair(197, "multianewarray"),
            new Pair(198, "ifnull"),
            new Pair(199, "ifnonnull")
        );
    }

    /**
     * Pair of opcode and its name.
     * @since 0.12.0
     */
    private static class Pair implements Map.Entry<Integer, String> {
        /**
         * Opcode key number.
         */
        private final int key;

        /**
         * Opcode name.
         */
        private final String name;

        /**
         * Constructor for creating a pair of opcode and its name.
         * @param key Opcode key number
         * @param value Opcode name
         */
        Pair(final int key, final String value) {
            this.key = key;
            this.name = value;
        }

        @Override
        public Integer getKey() {
            return this.key;
        }

        @Override
        public String getValue() {
            return this.name;
        }

        @Override
        public String setValue(final String value) {
            throw new UnsupportedOperationException("setValue is not supported for Pair entry");
        }
    }
}
