/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.objectweb.asm.ConstantDynamic;
import org.xembly.Directive;

/**
 * Directives for a JVM dynamic constant.
 * @since 0.18.0
 */
public final class DirectivesConstantDynamic implements Iterable<Directive> {

    /**
     * Operand index.
     */
    private final int index;

    /**
     * Directives format.
     */
    private final Format format;

    /**
     * Dynamic constant.
     */
    private final ConstantDynamic constant;

    /**
     * Constructor.
     * @param index Operand index
     * @param format Directives format
     * @param constant Dynamic constant
     */
    public DirectivesConstantDynamic(
        final int index, final Format format, final ConstantDynamic constant
    ) {
        this.index = index;
        this.format = format;
        this.constant = constant;
    }

    @Override
    public java.util.Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "constant-dynamic",
            new NumName("c", this.index).toString(),
            new DirectivesValue(0, this.format, this.constant.getName()),
            new DirectivesValue(1, this.format, this.constant.getDescriptor()),
            new DirectivesHandle(2, this.format, this.constant.getBootstrapMethod()),
            new DirectivesSeq(
                "arguments",
                IntStream.range(0, this.constant.getBootstrapMethodArgumentCount())
                    .mapToObj(
                        idx -> new DirectivesOperand(
                            idx,
                            this.format,
                            this.constant.getBootstrapMethodArgument(idx)
                        )
                    ).collect(Collectors.toList())
            )
        ).iterator();
    }
}
