package org.eolang.jeo;

import java.util.Base64;
import org.cactoos.Input;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;

public class Base64Bytecode {

    private final byte[] bytes;

    public Base64Bytecode(final Input input) {
        this(new UncheckedBytes(new BytesOf(input)).asBytes());
    }

    public Base64Bytecode(final byte[] bytes) {
        this.bytes = bytes;
    }

    public String asString() {
        return Base64.getEncoder().encodeToString(this.bytes);
    }
}
