/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.eolang.jeo.representation.bytecode.BytecodeMaxs;
import org.eolang.jeo.representation.bytecode.BytecodeValue;
import org.eolang.jeo.representation.directives.DirectivesJeoObject;
import org.eolang.jeo.representation.directives.DirectivesMaxs;
import org.eolang.jeo.representation.directives.DirectivesValue;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlMaxs}.
 *
 * @since 0.15.0
 */
final class XmlMaxsTest {

    @Test
    void convertsMaxsToBytecode() throws ImpossibleModificationException {
        final int stack = 3;
        final int locals = 5;
        MatcherAssert.assertThat(
            "XmlMaxs should parse stack and locals from valid XML node",
            new XmlMaxs(
                new XmlJeoObject(
                    new JcabiXmlNode(
                        new Xembler(new DirectivesMaxs(new Format(), stack, locals)).xml()
                    )
                )
            ).bytecode(),
            Matchers.equalTo(new BytecodeMaxs(stack, locals))
        );
    }

    @Test
    void doesNotSerializeXmlNodeOnSuccessfulRead() throws ImpossibleModificationException {
        final AtomicInteger calls = new AtomicInteger(0);
        new XmlMaxs(
            new XmlJeoObject(
                new XmlMaxsTest.SpyXmlNode(
                    new JcabiXmlNode(
                        new Xembler(new DirectivesMaxs(new Format(), 2, 4)).xml()
                    ),
                    calls
                )
            )
        ).bytecode();
        MatcherAssert.assertThat(
            "XmlMaxs must not serialize XML node when operands are non-null",
            calls.get(),
            Matchers.equalTo(0)
        );
    }

    @Test
    void formatsNodeInErrorMessageWhenOperandIsNull() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Error message should include position and node representation on failure",
            Assertions.assertThrows(
                NullPointerException.class,
                () -> new XmlMaxs(
                    new XmlJeoObject(XmlMaxsTest.nullOperandNode(new AtomicInteger(0)))
                ).bytecode(),
                "XmlMaxs should throw NullPointerException when operand value is missing"
            ).getMessage(),
            Matchers.allOf(
                Matchers.containsString("at '0' position"),
                Matchers.containsString("SpyXmlNode")
            )
        );
    }

    @Test
    void serializesNodeLazilyWhenOperandIsNull() throws ImpossibleModificationException {
        final AtomicInteger calls = new AtomicInteger(0);
        Assertions.assertThrows(
            NullPointerException.class,
            () -> new XmlMaxs(new XmlJeoObject(XmlMaxsTest.nullOperandNode(calls))).bytecode(),
            "XmlMaxs should throw NullPointerException when operand value is missing"
        );
        MatcherAssert.assertThat(
            "XmlMaxs should serialize node lazily only on failure",
            calls.get(),
            Matchers.greaterThan(0)
        );
    }

    @Test
    void checksEquality() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Two XmlMaxs with same stack and locals should be equal",
            new XmlMaxs(
                new XmlJeoObject(
                    new JcabiXmlNode(new Xembler(new DirectivesMaxs(new Format(), 1, 2)).xml())
                )
            ),
            Matchers.equalTo(
                new XmlMaxs(
                    new XmlJeoObject(
                        new JcabiXmlNode(new Xembler(new DirectivesMaxs(new Format(), 1, 2)).xml())
                    )
                )
            )
        );
    }

    @Test
    void checksHashCode() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Two equal XmlMaxs should have identical hash codes",
            new XmlMaxs(
                new XmlJeoObject(
                    new JcabiXmlNode(new Xembler(new DirectivesMaxs(new Format(), 1, 2)).xml())
                )
            ).hashCode(),
            Matchers.equalTo(
                new XmlMaxs(
                    new XmlJeoObject(
                        new JcabiXmlNode(new Xembler(new DirectivesMaxs(new Format(), 1, 2)).xml())
                    )
                ).hashCode()
            )
        );
    }

    @Test
    void checksToString() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "XmlMaxs toString should contain class name",
            new XmlMaxs(
                new XmlJeoObject(
                    new JcabiXmlNode(new Xembler(new DirectivesMaxs(new Format(), 1, 2)).xml())
                )
            ).toString(),
            Matchers.containsString("XmlMaxs(node=")
        );
    }

    private static XmlNode nullOperandNode(final AtomicInteger calls)
        throws ImpossibleModificationException {
        return new XmlMaxsTest.SpyXmlNode(
            new JcabiXmlNode(
                new Xembler(
                    new DirectivesJeoObject(
                        "maxs",
                        "maxs",
                        new DirectivesValue(new Format(), "m1", new BytecodeValue(null)),
                        new DirectivesValue(new Format(), "m2", 0)
                    )
                ).xml()
            ),
            calls
        );
    }

    /**
     * Spy wrapper around {@link XmlNode} that counts {@link #toString()} calls.
     *
     * @since 0.15.0
     */
    private static final class SpyXmlNode implements XmlNode {

        /**
         * Wrapped XML node.
         */
        private final XmlNode origin;

        /**
         * Counter of {@code toString()} invocations.
         */
        private final AtomicInteger counter;

        /**
         * Constructor.
         *
         * @param origin Wrapped XML node
         * @param counter Counter for {@code toString()} invocations
         */
        private SpyXmlNode(final XmlNode origin, final AtomicInteger counter) {
            this.origin = origin;
            this.counter = counter;
        }

        @Override
        public Stream<XmlNode> children() {
            return this.origin.children();
        }

        @Override
        public String text() {
            return this.origin.text();
        }

        @Override
        public Optional<String> attribute(final String name) {
            return this.origin.attribute(name);
        }

        @Override
        public XmlNode child(final String name) {
            return this.origin.child(name);
        }

        @Override
        public List<String> xpath(final String xpath) {
            return this.origin.xpath(xpath);
        }

        @Override
        public void validate() {
            this.origin.validate();
        }

        @Override
        public String toString() {
            this.counter.incrementAndGet();
            return String.format("SpyXmlNode(%s)", this.origin);
        }
    }
}
