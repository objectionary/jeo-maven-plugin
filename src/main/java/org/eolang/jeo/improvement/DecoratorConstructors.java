/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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
package org.eolang.jeo.improvement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.eolang.jeo.representation.xmir.XmlBytecodeEntry;
import org.eolang.jeo.representation.xmir.XmlClass;
import org.eolang.jeo.representation.xmir.XmlInstruction;
import org.eolang.jeo.representation.xmir.XmlLabel;
import org.eolang.jeo.representation.xmir.XmlMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * This class tries to combine constructors of the decorated class and the decorator.
 * @since 0.1
 */
class DecoratorConstructors {

    /**
     * Decorated class.
     */
    private final XmlClass decorated;

    /**
     * Class that decorates {@link #decorated}.
     */
    private final XmlClass decorator;

    private final String combined;

    /**
     * Constructor.
     * @param decorated Decorated class.
     * @param decorator Class that decorates {@link #decorated}.
     * @param combined
     */
    DecoratorConstructors(
        final XmlClass decorated,
        final XmlClass decorator,
        final String combined
    ) {
        this.decorated = decorated;
        this.decorator = decorator;
        this.combined = combined;
    }

    /**
     * Returns a list of constructors of the decorator class.
     * @return List of constructors.
     */
    List<XmlMethod> constructors() {
        final List<XmlMethod> alldecorated = this.decorated.constructors();
        final List<XmlMethod> alldecoratee = this.decorator.constructors();
        final List<XmlMethod> result = new ArrayList<>(alldecorated.size() + alldecoratee.size());
        for (final XmlMethod origin : alldecorated) {
            for (final XmlMethod upper : alldecoratee) {
                result.add(
                    new XmlMethod(
                        upper.name(),
                        upper.access(),
                        this.combineDescriptors(origin.descriptor(), upper.descriptor()),
                        this.combineInstructions(origin, upper)
                    )
                );
            }
        }
        return result;
    }

    /**
     * Combines descriptors of the decorated and decorator constructors.
     * @param decored Decorated constructor descriptor.
     * @param decoror Decorator constructor descriptor.
     * @return Combined descriptor.
     */
    private String combineDescriptors(
        final String decored, final String decoror
    ) {
        final String aim = this.decorated.name();
        final Type[] replacement = Type.getType(decored).getArgumentTypes();
        final Type[] array = Arrays.stream(Type.getType(decoror).getArgumentTypes())
            .flatMap(
                type -> {
                    final Stream<? extends Type> result;
                    if (type.getClassName().replace('.', '/').equals(aim)) {
                        result = Arrays.stream(replacement);
                    } else {
                        result = Stream.of(type);
                    }
                    return result;
                }
            ).toArray(Type[]::new);
        return Type.getMethodType(Type.VOID_TYPE, array).getDescriptor();
    }

    /**
     * Combines instructions of the decorated and decorator constructors.
     * @param decored Decorated constructor.
     * @param decoror Decorator constructor.
     * @return Combined instructions.
     */
    private XmlBytecodeEntry[] combineInstructions(
        final XmlMethod decored,
        final XmlMethod decoror
    ) {
        return Stream.concat(
            decored.instructions(new XmlMethod.Without(Opcodes.RETURN)).stream(),
            this.decoratorInstructions(decoror)
        ).toArray(XmlBytecodeEntry[]::new);
    }

    private Stream<XmlBytecodeEntry> decoratorInstructions(final XmlMethod decoror) {
        return decoror.instructions()
            .stream()
            .filter(instruction -> {
                if (instruction instanceof XmlInstruction) {
                    final XmlInstruction xmlinstr = (XmlInstruction) instruction;
                    if (xmlinstr.hasOpcode(Opcodes.PUTFIELD)) {
                        final Object what = xmlinstr.arguments()[2];
                        return !this.decorated.name().equals(
                            Type.getType((String) what).getClassName().replace('.', '/')
                        );
                    }
                }
                return true;
            })
            .map(instruction -> {
                    if (instruction instanceof XmlInstruction) {
                        final XmlInstruction xmlinstr = (XmlInstruction) instruction;
                        if (xmlinstr.hasOpcode(Opcodes.ALOAD)) {
                            final Object argument = xmlinstr.arguments()[0];
                            if (argument instanceof Integer) {
                                if ((int) argument > 0) {
                                    return new XmlInstruction(Opcodes.ILOAD, argument);
                                }
                            }
                        }
                        List<Object> newargs = new ArrayList<>(xmlinstr.arguments().length);
                        for (final Object argument : xmlinstr.arguments()) {
                            if (argument instanceof String) {
                                final String sarg = (String) argument;
                                newargs.add(sarg.replace("L" + this.decorated.name() + ";", "I"));
                            } else {
                                newargs.add(argument);
                            }
                        }
                        return new XmlInstruction(xmlinstr.code(), newargs.toArray());
                    } else {
                        return instruction;
                    }
                }
            );
    }

}
