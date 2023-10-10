package org.eolang.jeo.representation;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

public class HexData {

    private final Object data;

    public HexData(final Object data) {
        this.data = data;
    }

    /**
     * Value of the data.
     * @return Value
     */
    public String value() {
        final byte[] res;
        if (this.data instanceof String) {
            res = ((String) this.data).getBytes(StandardCharsets.UTF_8);
        } else {
            res = ByteBuffer
                .allocate(Long.BYTES)
                .putLong((int) this.data)
                .array();
        }
        return HexData.bytesToHex(res);
    }

    /**
     * Type of the data.
     * @return Type
     */
    public String type() {
        final String res;
        if (this.data instanceof String) {
            res = "string";
        } else {
            res = "int";
        }
        return res;
    }

    /**
     * Bytes to HEX.
     *
     * @param bytes Bytes.
     * @return Hexadecimal value as string.
     */
    private static String bytesToHex(final byte... bytes) {
        final StringJoiner out = new StringJoiner(" ");
        for (final byte bty : bytes) {
            out.add(String.format("%02X", bty));
        }
        return out.toString();
    }
}
