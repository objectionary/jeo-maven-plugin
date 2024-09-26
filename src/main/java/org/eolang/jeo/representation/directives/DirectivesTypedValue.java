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
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import java.util.Objects;
import java.util.Random;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.DataType;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Typed data directives.
 *
 * @since 0.3
 * @todo #705:60min Do we need to add a scope attribute to the directives if the data is null?
 *  If we do, then we need to add a test for this case.
 *  If we don't, then we need to remove this puzzle and "scope" attribute from the directives.
 */
@ToString
@EqualsAndHashCode
public final class DirectivesTypedValue implements Iterable<Directive> {

    /**
     * Name of the value.
     */
    private final String name;

    /**
     * Value.
     */
    private final Object data;

    /**
     * Type of the value.
     */
    private final Type type;

    /**
     * Constructor.
     * @param name Name of the value
     * @param data Value
     * @param descriptor Type descriptor
     */
    public DirectivesTypedValue(final String name, final Object data, final String descriptor) {
        this(name, data, Type.getType(descriptor));
    }

    /**
     * Constructor.
     * @param name Name of the value
     * @param data Value
     * @param type Type of the value
     */
    public DirectivesTypedValue(final String name, final Object data, final Type type) {
        this.name = name;
        this.data = data;
        this.type = type;
    }

    @Override
    public Iterator<Directive> iterator() {
        try {
            final DataType base = DataType.find(this.type);
            final Directives directives = new Directives().add("o")
                .attr("base", base.fqn())
                .attr("line", new Random().nextInt(Integer.MAX_VALUE));
            final String hex = base.toHexString(this.data);
            if (!this.name.isEmpty()) {
                directives.attr("name", this.name);
            }
            if (Objects.isNull(hex)) {
                directives.attr("scope", "nullable");
            } else {
                directives.append(new DirectivesBytes(hex));
            }
            return directives.up().iterator();
        } catch (final IllegalArgumentException | ClassCastException exception) {
            throw new IllegalStateException(
                String.format("Failed to create directives for %s", this), exception
            );
        }
    }
}
