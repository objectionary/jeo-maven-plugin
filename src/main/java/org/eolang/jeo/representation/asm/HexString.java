package org.eolang.jeo.representation.asm;

import java.util.Arrays;
import java.util.stream.Collectors;

public class HexString {

    private final String hex;

    public HexString(final String hex) {
        this.hex = hex;
    }

    /**
     * Convert hex string to human-readable string.
     * Example:
     *  "48 65 6C 6C 6F 20 57 6F 72 6C 64 21" -> "Hello World!"
     * @return Human-readable string.
     */
    String decode() {
        return Arrays.stream(hex.split(" "))
            .map(ch -> (char) Integer.parseInt(ch, 16))
            .map(String::valueOf)
            .collect(Collectors.joining());
    }

    int decodeAsInt() {
        return Arrays.stream(this.hex.split(" "))
            .mapToInt(ch -> Integer.parseInt(ch, 16))
            .sum();
    }
}
