/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.DirectivesEnclosingMethod;
import org.eolang.jeo.representation.directives.DirectivesNestHost;
import org.eolang.jeo.representation.directives.DirectivesNestMembers;
import org.eolang.jeo.representation.directives.DirectivesPermittedSubclasses;
import org.eolang.jeo.representation.directives.DirectivesRecordComponents;
import org.eolang.jeo.representation.directives.DirectivesSourceFile;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.xembly.Directive;

/**
 * Bytecode attribute.
 *
 * @since 0.4
 */
public interface BytecodeAttribute {

    /**
     * Write to class.
     *
     * @param clazz Bytecode where to write
     */
    void write(ClassVisitor clazz);

    /**
     * Write to method.
     *
     * @param method Bytecode where to write
     * @param labels Method labels
     */
    void write(MethodVisitor method, AsmLabels labels);

    /**
     * Converts to directives.
     *
     * @param index Index of the attribute
     * @param format Format of the directives
     * @return Directives
     */
    Iterable<Directive> directives(int index, Format format);

    /**
     * Source file attribute.
     *
     * @since 0.14.0
     */
    final class SourceFile implements BytecodeAttribute {

        /**
         * The name of the source file from which this class was compiled.
         * May be null.
         */
        private final String source;

        /**
         * The correspondence between source and compiled elements of this class.
         * May be null.
         */
        private final String debug;

        /**
         * Constructor.
         *
         * @param source Name of the source file
         * @param debug Debug information
         */
        public SourceFile(final String source, final String debug) {
            this.source = source;
            this.debug = debug;
        }

        @Override
        public void write(final ClassVisitor clazz) {
            clazz.visitSource(this.source, this.debug);
        }

        @Override
        public void write(final MethodVisitor method, final AsmLabels labels) {
            throw new UnsupportedOperationException(
                "SourceFile attribute cannot be written to method"
            );
        }

        @Override
        public Iterable<Directive> directives(final int index, final Format format) {
            return new DirectivesSourceFile(format, this.source, this.debug);
        }

        @Override
        public boolean equals(final Object other) {
            final boolean result;
            if (this == other) {
                result = true;
            } else if (other instanceof SourceFile) {
                final SourceFile file = (SourceFile) other;
                result = Objects.equals(this.source, file.source)
                    && Objects.equals(this.debug, file.debug);
            } else {
                result = false;
            }
            return result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.source, this.debug);
        }

        @Override
        public String toString() {
            return String.format(
                "SourceFile(source=%s, debug=%s)", this.source, this.debug
            );
        }
    }

    /**
     * Enclosing method attribute.
     *
     * @since 0.14.0
     */
    final class EnclosingMethod implements BytecodeAttribute {

        /**
         * Owner class of the enclosing method.
         */
        private final String owner;

        /**
         * Method name of the enclosing method.
         * May be null.
         */
        private final String name;

        /**
         * Method descriptor of the enclosing method.
         * May be null.
         */
        private final String descriptor;

        /**
         * Constructor.
         *
         * @param owner Owner class of the enclosing method
         * @param name Method name of the enclosing method
         * @param descriptor Method descriptor of the enclosing method
         */
        public EnclosingMethod(final String owner, final String name, final String descriptor) {
            this.owner = owner;
            this.name = name;
            this.descriptor = descriptor;
        }

        @Override
        public void write(final ClassVisitor clazz) {
            clazz.visitOuterClass(this.owner, this.name, this.descriptor);
        }

        @Override
        public void write(final MethodVisitor method, final AsmLabels labels) {
            throw new UnsupportedOperationException("EnclosingMethod cannot be written to method");
        }

        @Override
        public Iterable<Directive> directives(final int index, final Format format) {
            return new DirectivesEnclosingMethod(format, this.owner, this.name, this.descriptor);
        }

        @Override
        public boolean equals(final Object other) {
            final boolean result;
            if (this == other) {
                result = true;
            } else if (other instanceof EnclosingMethod) {
                final EnclosingMethod method = (EnclosingMethod) other;
                result = Objects.equals(this.owner, method.owner)
                    && Objects.equals(this.name, method.name)
                    && Objects.equals(this.descriptor, method.descriptor);
            } else {
                result = false;
            }
            return result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.owner, this.name, this.descriptor);
        }

        @Override
        public String toString() {
            return String.format(
                "EnclosingMethod(owner=%s, name=%s, descriptor=%s)",
                this.owner, this.name, this.descriptor
            );
        }
    }

    /**
     * Nest host attribute.
     *
     * @since 0.14.0
     */
    final class NestHost implements BytecodeAttribute {

        /**
         * Host class of the nest.
         */
        private final String host;

        /**
         * Constructor.
         *
         * @param host Host class of the nest
         */
        public NestHost(final String host) {
            this.host = host;
        }

        @Override
        public void write(final ClassVisitor clazz) {
            clazz.visitNestHost(this.host);
        }

        @Override
        public void write(final MethodVisitor method, final AsmLabels labels) {
            throw new UnsupportedOperationException(
                "NestHost attribute cannot be written to method"
            );
        }

        @Override
        public Iterable<Directive> directives(final int index, final Format format) {
            return new DirectivesNestHost(format, this.host);
        }

        @Override
        public boolean equals(final Object other) {
            final boolean result;
            if (this == other) {
                result = true;
            } else if (other instanceof NestHost) {
                result = Objects.equals(this.host, ((NestHost) other).host);
            } else {
                result = false;
            }
            return result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.host);
        }

        @Override
        public String toString() {
            return String.format("NestHost(host=%s)", this.host);
        }
    }

    /**
     * Nest members attribute.
     *
     * @since 0.14.0
     */
    final class NestMembers implements BytecodeAttribute {

        /**
         * Nest members.
         */
        private final List<String> members;

        /**
         * Constructor.
         *
         * @param nested Nest members
         */
        public NestMembers(final String... nested) {
            this(Arrays.asList(nested));
        }

        /**
         * Constructor.
         *
         * @param nested Nest members
         */
        public NestMembers(final List<String> nested) {
            this.members = nested;
        }

        @Override
        public void write(final ClassVisitor clazz) {
            this.members.forEach(clazz::visitNestMember);
        }

        @Override
        public void write(final MethodVisitor method, final AsmLabels labels) {
            throw new UnsupportedOperationException("NestMembers cannot be written to method");
        }

        @Override
        public Iterable<Directive> directives(final int index, final Format format) {
            return new DirectivesNestMembers(format, this.members);
        }

        @Override
        public boolean equals(final Object other) {
            final boolean result;
            if (this == other) {
                result = true;
            } else if (other instanceof NestMembers) {
                result = Objects.equals(this.members, ((NestMembers) other).members);
            } else {
                result = false;
            }
            return result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.members);
        }

        @Override
        public String toString() {
            return String.format("NestMembers(members=%s)", this.members);
        }
    }

    /**
     * Permitted subclasses attribute.
     *
     * @since 0.14.0
     */
    final class PermittedSubclasses implements BytecodeAttribute {

        /**
         * Permitted subclasses.
         */
        private final List<String> classes;

        /**
         * Constructor.
         *
         * @param all Permitted subclasses
         */
        public PermittedSubclasses(final String... all) {
            this(Arrays.asList(all));
        }

        /**
         * Constructor.
         *
         * @param classes Permitted subclasses
         */
        public PermittedSubclasses(final List<String> classes) {
            this.classes = classes;
        }

        @Override
        public void write(final ClassVisitor clazz) {
            this.classes.forEach(clazz::visitPermittedSubclass);
        }

        @Override
        public void write(final MethodVisitor method, final AsmLabels labels) {
            throw new UnsupportedOperationException(
                "PermittedSubclasses attribute cannot be written to method"
            );
        }

        @Override
        public Iterable<Directive> directives(final int index, final Format format) {
            return new DirectivesPermittedSubclasses(format, this.classes);
        }

        @Override
        public boolean equals(final Object other) {
            final boolean result;
            if (this == other) {
                result = true;
            } else if (other instanceof PermittedSubclasses) {
                result = Objects.equals(this.classes, ((PermittedSubclasses) other).classes);
            } else {
                result = false;
            }
            return result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.classes);
        }

        @Override
        public String toString() {
            return String.format("PermittedSubclasses(classes=%s)", this.classes);
        }
    }

    /**
     * Record components attribute.
     *
     * @since 0.14.0
     */
    final class RecordComponents implements BytecodeAttribute {

        /**
         * Components.
         */
        private final List<BytecodeRecordComponent> components;

        /**
         * Constructor.
         *
         * @param components Components
         */
        public RecordComponents(final BytecodeRecordComponent... components) {
            this(Arrays.asList(components));
        }

        /**
         * Constructor.
         *
         * @param components Components
         */
        public RecordComponents(final List<BytecodeRecordComponent> components) {
            this.components = components;
        }

        @Override
        public void write(final ClassVisitor clazz) {
            this.components.forEach(c -> c.write(clazz));
        }

        @Override
        public void write(final MethodVisitor method, final AsmLabels labels) {
            throw new UnsupportedOperationException(
                "RecordComponents attribute cannot be written to method"
            );
        }

        @Override
        public Iterable<Directive> directives(final int index, final Format format) {
            final AtomicInteger counter = new AtomicInteger(0);
            return new DirectivesRecordComponents(
                index,
                this.components.stream()
                    .map(component -> component.directives(counter.getAndIncrement(), format))
                    .collect(Collectors.toList())
            );
        }

        @Override
        public boolean equals(final Object other) {
            final boolean result;
            if (this == other) {
                result = true;
            } else if (other instanceof RecordComponents) {
                result = Objects.equals(this.components, ((RecordComponents) other).components);
            } else {
                result = false;
            }
            return result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.components);
        }

        @Override
        public String toString() {
            return String.format("RecordComponents(components=%s)", this.components);
        }
    }
}
