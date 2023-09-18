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
package org.eolang.jeo.representation;

import java.util.Base64;

/**
 * Bytecode that represents a Hello World program.
 *
 * @since 0.1.0
 */
public final class HelloWorldBytecode {

    /**
     * Bytecode in Base64 form.
     */
    private final String encoded;

    /**
     * Constructor.
     */
    HelloWorldBytecode() {
        this(HelloWorldBytecode.defaultListing());
    }

    /**
     * Constructor.
     * @param encoded Bytecode in Base64 form.
     */
    private HelloWorldBytecode(final String encoded) {
        this.encoded = encoded;
    }

    /**
     * Convert to bytes.
     * @return Bytes.
     */
    public byte[] bytes() {
        return Base64.getDecoder().decode(this.encoded).clone();
    }

    /**
     * Convert to Base64.
     * @return Base64.
     * @checkstyle MethodNameCheck (3 lines)
     */
    String base64() {
        return this.encoded;
    }

    /**
     * Default listing of bytecode of a Hello World program.
     * @return Listing.
     */
    private static String defaultListing() {
        return String.join(
            "",
            "yv66vgAAADQAIgoAAgADBwAEDAAFAAYBABBqYXZhL2xhbmcvT2JqZWN0AQAGPGluaXQ+AQADKClWCQAIAAk",
            "HAAoMAAsADAEAEGphdmEvbGFuZy9TeXN0ZW0BAANvdXQBABVMamF2YS9pby9QcmludFN0cmVhbTsIAA4BAA1",
            "IZWxsbywgV29ybGQhCgAQABEHABIMABMAFAEAE2phdmEvaW8vUHJpbnRTdHJlYW0BAAdwcmludGxuAQAVKEx",
            "qYXZhL2xhbmcvU3RyaW5nOylWBwAWAQAab3JnL2VvbGFuZy9qZW8vQXBwbGljYXRpb24BAARDb2RlAQAPTGl",
            "uZU51bWJlclRhYmxlAQASTG9jYWxWYXJpYWJsZVRhYmxlAQAEdGhpcwEAHExvcmcvZW9sYW5nL2plby9BcHB",
            "saWNhdGlvbjsBAARtYWluAQAWKFtMamF2YS9sYW5nL1N0cmluZzspVgEABGFyZ3MBABNbTGphdmEvbGFuZy9",
            "TdHJpbmc7AQAKU291cmNlRmlsZQEAEEFwcGxpY2F0aW9uLmphdmEAIQAVAAIAAAAAAAIAAQAFAAYAAQAXAAA",
            "ALwABAAEAAAAFKrcAAbEAAAACABgAAAAGAAEAAAADABkAAAAMAAEAAAAFABoAGwAAAAkAHAAdAAEAFwAAADc",
            "AAgABAAAACbIABxINtgAPsQAAAAIAGAAAAAoAAgAAAAUACAAGABkAAAAMAAEAAAAJAB4AHwAAAAEAIAAAAAI",
            "AIQ=="
        );
    }
}
