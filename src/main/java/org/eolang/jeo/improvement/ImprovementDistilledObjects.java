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

import com.jcabi.log.Logger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.Improvement;
import org.eolang.jeo.Representation;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.objectweb.asm.Opcodes;

/**
 * Distilled objects improvement.
 * You can find the description of the optimization right
 * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/102">here</a>
 * @since 0.1.0
 */
public class ImprovementDistilledObjects implements Improvement {
    @Override
    public Collection<? extends Representation> apply(
        final Collection<? extends Representation> representations
    ) {
        final List<Representation> additional = ImprovementDistilledObjects
            .decorators(new ArrayList<Representation>(representations))
            .stream()
            .map(DecoratorPair::combine)
            .collect(Collectors.toList());
        Logger.info(
            this,
            String.format(
                "Distilled objects improvement is successfully applied. Generated classes: %s, total %d",
                additional.stream().map(Representation::name).collect(Collectors.toList()),
                additional.size()
            )
        );
        return Stream.concat(
            representations.stream(),
            additional.stream()
        ).collect(Collectors.toList());
    }

    /**
     * Find decorators.
     * @param eobjects EObjects.
     * @return Decorators.
     * @todo #152:90min Implement decorator search.
     *  Right now we just return the first two EObjects as decorators which is not correct.
     *  We need to implement a proper decorator search. When it's done, remove that puzzle.
     */
    private static Collection<DecoratorPair> decorators(List<? extends Representation> eobjects) {
        final Collection<DecoratorPair> result;
        if (eobjects.isEmpty() || eobjects.size() < 2) {
            result = Collections.emptyList();
        } else {
            result = Collections.singleton(new DecoratorPair(eobjects.get(0), eobjects.get(1)));
        }
        return result;
    }

    /**
     * Decorator pair.
     * Pair of XMIR files where the first one is a decorator and the second one is a decorated object.
     * @since 0.1.0
     */
    private static class DecoratorPair {

        /**
         * Decorated object.
         */
        private final Representation decorated;

        /**
         * Object that decorates.
         */
        private final Representation decorator;

        /**
         * Constructor.
         * @param decorated Decorated object.
         * @param decorator Object that decorates.
         */
        DecoratorPair(Representation decorated, Representation decorator) {
            this.decorated = decorated;
            this.decorator = decorator;
        }

        /**
         * Combine two representations into one.
         * @return Combined representation.
         * @todo #152:90min Implement decorator combination.
         *  Right now we just return dummy object which is empty.
         *  We need to implement a proper decorator combination.
         *  Don't forget to add unit tests for the method.
         */
        private Representation combine() {
            final String second = this.decorator.name();
            return new BytecodeRepresentation(
                new BytecodeClass(
                    String.format(
                        "%s%s",
                        this.decorated.name(),
                        second.substring(second.lastIndexOf('.') + 1)
                    ),
                    Opcodes.ACC_PUBLIC
                ).bytecode().asBytes()
            );
        }
    }

}
