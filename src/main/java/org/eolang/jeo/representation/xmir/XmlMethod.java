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
package org.eolang.jeo.representation.xmir;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.ToString;
import org.eolang.jeo.representation.bytecode.BytecodeMethodProperties;
import org.eolang.jeo.representation.directives.DirectivesMethod;
import org.eolang.jeo.representation.directives.DirectivesMethodProperties;
import org.objectweb.asm.Opcodes;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * XML method.
 * @since 0.1
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
@ToString
public final class XmlMethod {

    /**
     * Method node.
     */
    @ToString.Include
    private final XmlNode node;

    /**
     * Constructor.
     */
    public XmlMethod() {
        this("foo", Opcodes.ACC_PUBLIC, "()V");
    }

    /**
     * Constructor.
     * @param name Method name.
     * @param access Access modifiers.
     * @param descriptor Method descriptor.
     * @param exceptions Method exceptions.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public XmlMethod(
        final String name,
        final int access,
        final String descriptor,
        final String... exceptions
    ) {
        this(XmlMethod.prestructor(name, access, descriptor, exceptions));
    }

    /**
     * Constructor.
     * @param xmlnode Method node.
     */
    public XmlMethod(final XmlNode xmlnode) {
        this.node = xmlnode;
    }

    /**
     * Method name.
     * @return Name.
     */
    public String name() {
        final String result;
        final String original = this.node.attribute("name").orElseThrow();
        if ("new".equals(original)) {
            result = "<init>";
        } else {
            result = original;
        }
        return result;
    }

    /**
     * Method access modifiers.
     * @return Access modifiers.
     */
    public int access() {
        return new HexString(this.node.child("name", "access").text()).decodeAsInt();
    }

    /**
     * Method descriptor.
     * @return Descriptor.
     */
    public String descriptor() {
        return new HexString(this.node.child("name", "descriptor").text()).decode();
    }

    /**
     * Method signature.
     * @return Signature.
     */
    public String signature() {
        return this.node.optchild("name", "signature")
            .map(XmlNode::text)
            .filter(s -> !s.isBlank())
            .map(HexString::new)
            .map(HexString::decode)
            .orElse(null);
    }

    /**
     * Method trycatch entries.
     * @return Trycatch entries.
     */
    public List<XmlTryCatchEntry> trycatchEntries() {
        return this.node.children()
            .filter(element -> element.hasAttribute("name", "trycatchblocks"))
            .flatMap(XmlNode::children)
            .map(XmlTryCatchEntry::new)
            .collect(Collectors.toList());
    }

    /**
     * Method properties as bytecode.
     * @return Properties.
     */
    public BytecodeMethodProperties properties() {
        return new BytecodeMethodProperties(
            this.access(),
            this.name(),
            this.descriptor(),
            this.signature(),
            this.exceptions()
        );
    }

    /**
     * Method max stack and locals.
     * @return Maxs.
     */
    public XmlMaxs maxs() {
        return new XmlMaxs(this.node.child("name", "maxs"));
    }

    /**
     * Checks if method is a constructor.
     * @return True if method is a constructor.
     */
    public boolean isConstructor() {
        return this.node.hasAttribute("name", "new");
    }

    /**
     * All method instructions.
     * @param predicates Predicates to filter instructions.
     * @return Instructions.
     */
    @SafeVarargs
    public final List<XmlBytecodeEntry> instructions(
        final Predicate<XmlBytecodeEntry>... predicates
    ) {
        return this.node.child("base", "seq")
            .child("base", "tuple")
            .children()
            .filter(element -> element.attribute("base").isPresent())
            .map(XmlNode::toEntry)
            .filter(instr -> Arrays.stream(predicates).allMatch(predicate -> predicate.test(instr)))
            .collect(Collectors.toList());
    }

    /**
     * All the method instructions.
     * @return Instructions.
     */
    public List<XmlNode> nodes() {
        return this.node.child("base", "seq")
            .child("base", "tuple")
            .children()
            .collect(Collectors.toList());
    }

    /**
     * Replace instructions.
     * @param entries Instructions to replace.
     * @todo #350 Remove mutable method from XmlMethod.
     *  Here we just remove all instructions and add new ones, this makes XmlMethod class
     *  mutable, which is a significant architecture flaw. It's much better to
     *  implement copying of this class with creation of a new XmlMethod, but in order to
     *  implement this we will have to implement copying all the top level classes like
     *  XmlProgram, XmlClass and so on, which requires lots of work.
     */
    public void replaceInstructions(final XmlNode... entries) {
        final XmlNode seq = this.node.child("base", "seq")
            .child("base", "tuple");
        seq.children()
            .filter(element -> element.attribute("base").isPresent())
            .forEach(XmlNode::erase);
        for (final XmlNode entry : entries) {
            seq.append(entry);
        }
    }

    /**
     * Method exceptions.
     * @return Exceptions.
     */
    private String[] exceptions() {
        return this.node.child("name", "exceptions")
            .children()
            .map(XmlNode::text)
            .map(HexString::new)
            .map(HexString::decode)
            .toArray(String[]::new);
    }

    /**
     * Create Method XmlNode by directives.
     * @param name Method name.
     * @param access Method access modifiers.
     * @param descriptor Method descriptor.
     * @param exceptions Method exceptions.
     * @return Method XmlNode.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    private static XmlNode prestructor(
        final String name,
        final int access,
        final String descriptor,
        final String... exceptions
    ) {
        return new XmlNode(
            new Xembler(
                new DirectivesMethod(
                    name,
                    new DirectivesMethodProperties(access, descriptor, "", exceptions)
                ),
                new Transformers.Node()
            ).xmlQuietly()
        );
    }
}
