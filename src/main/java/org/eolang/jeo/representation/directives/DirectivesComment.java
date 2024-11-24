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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives for a comment.
 * @since 0.6
 */
public final class DirectivesComment implements Iterable<Directive> {

    /**
     * Unsafe characters.
     */
    private static final Pattern UNSAFE_CHARS = Pattern.compile("[&<>'-]");

    /**
     * Chars that are discouraged in XML.
     * By this pattern, we remove all discouraged characters from the comment.
     */
    private static final Pattern DISCOURAGED = Pattern.compile(
        String.join(
            "|",
            "[\\x00-\\x08]",
            "[\\x{1}-\\x{8}]",
            "[\\x{B}-\\x{C}]",
            "[\\x{E}-\\x{1F}]",
            "[\\x{7F}-\\x{84}]",
            "[\\x{86}-\\x{9F}]",
            "[\\x{FDD0}-\\x{FDDF}]",
            "[\\x{1FFFE}-\\x{1FFFF}]",
            "[\\x{2FFFE}-\\x{2FFFF}]",
            "[\\x{3FFFE}-\\x{3FFFF}]",
            "[\\x{4FFFE}-\\x{4FFFF}]",
            "[\\x{5FFFE}-\\x{5FFFF}]",
            "[\\x{6FFFE}-\\x{6FFFF}]",
            "[\\x{7FFFE}-\\x{7FFFF}]",
            "[\\x{8FFFE}-\\x{8FFFF}]",
            "[\\x{9FFFE}-\\x{9FFFF}]",
            "[\\x{AFFFE}-\\x{AFFFF}]",
            "[\\x{BFFFE}-\\x{BFFFF}]",
            "[\\x{CFFFE}-\\x{CFFFF}]",
            "[\\x{DFFFE}-\\x{DFFFF}]",
            "[\\x{EFFFE}-\\x{EFFFF}]",
            "[\\x{FFFFE}-\\x{FFFFF}]",
            "[\\x{10FFFE}-\\x{10FFFF}]"
        )
    );

    /**
     * Comment.
     */
    private final String comment;

    /**
     * Constructor.
     * @param comment Comment.
     */
    DirectivesComment(final String comment) {
        this.comment = comment;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Iterator<Directive> result;
        if (this.comment.isEmpty()) {
            result = new Directives().iterator();
        } else {
            result = new Directives().comment(
                String.format(" %s ", this.escaped())
            ).iterator();
        }
        return result;
    }

    /**
     * Escapes unsafe characters.
     * @return Escaped comment.
     */
    private String escaped() {
        final Matcher matcher = DirectivesComment.UNSAFE_CHARS.matcher(this.comment);
        final StringBuffer result = new StringBuffer(0);
        while (matcher.find()) {
            final String replacement;
            switch (matcher.group()) {
                case "&":
                    replacement = "&amp;";
                    break;
                case "<":
                    replacement = "&lt;";
                    break;
                case ">":
                    replacement = "&gt;";
                    break;
                case "'":
                    replacement = "&apos;";
                    break;
                case "-":
                    replacement = "&#45;";
                    break;
                default:
                    throw new IllegalStateException(
                        String.format("Unexpected value: %s", matcher.group())
                    );
            }
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        return DirectivesComment.DISCOURAGED.matcher(result).replaceAll("");
    }
}
