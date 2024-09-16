/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesMethodParams;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * Bytecode parameters.
 * @since 0.4
 */
@ToString
@EqualsAndHashCode
public final class BytecodeMethodParameters {

    /**
     * Annotations with a parameter position (as a key).
     */
    private final List<BytecodeMethodParameter> params;

    /**
     * Default constructor.
     */
    public BytecodeMethodParameters() {
        this(new ArrayList<>(0));
    }

    public BytecodeMethodParameters(final String descriptor) {
        this(BytecodeMethodParameters.fromDescriptor(descriptor));
    }

    /**
     * Constructor.
     * @param params Parameters.
     */
    public BytecodeMethodParameters(final BytecodeMethodParameter... params) {
        this(Arrays.asList(params));
    }

    /**
     * Constructor.
     * @param params Parameters.
     */
    public BytecodeMethodParameters(final List<BytecodeMethodParameter> params) {
        this.params = params;
    }

    /**
     * Add annotation.
     * @param visitor Method visitor.
     */
    public void write(final MethodVisitor visitor) {
        this.params.forEach(param -> param.write(visitor));
    }

    /**
     * Convert to directives.
     * @return Directives.
     */
    public DirectivesMethodParams directives() {
        return new DirectivesMethodParams(
            this.params.stream()
                .map(BytecodeMethodParameter::directives)
                .collect(Collectors.toList())
//            this.annotations.entrySet().stream().collect(
//                Collectors.toMap(
//                    Map.Entry::getKey,
//                    entry -> entry.getValue().stream()
//                        .map(BytecodeAnnotation::directives)
//                        .collect(Collectors.toList())
//                )
//            )
        );
    }

    private static List<BytecodeMethodParameter> fromDescriptor(final String descriptor) {
        final Type[] types = Type.getArgumentTypes(descriptor);
        final int size = types.length;
        final List<BytecodeMethodParameter> params = new ArrayList<>(size);
        for (int index = 0; index < size; ++index) {
            params.add(new BytecodeMethodParameter(index, types[index]));
        }
        return params;
    }
}
