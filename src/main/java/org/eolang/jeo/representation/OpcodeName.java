package org.eolang.jeo.representation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.sisu.space.asm.Opcodes;

final class OpcodeName {

    private static final Map<Integer, String> NAMES = OpcodeName.init();

    private static final AtomicInteger counter = new AtomicInteger(0);

    private final int opcode;

    OpcodeName(final int opcode) {
        this.opcode = opcode;
    }

    String asString() {
        return String.format(
            "%s-%d-%d",
            OpcodeName.NAMES.getOrDefault(this.opcode, "UNKNOWN"),
            this.opcode,
            OpcodeName.counter.incrementAndGet()
        );
    }

    private static Map<Integer, String> init() {
        try {
            Map<Integer, String> res = new HashMap<>();
            for (Field field : Opcodes.class.getFields()) {
                if (field.getType() == int.class) {
                    final String name = field.getName();
                    final int value = field.getInt(Opcodes.class);
                    res.put(value, name);
                }
            }
            return res;
        } catch (final IllegalAccessException exception) {
            throw new IllegalStateException(
                String.format("Can't retrieve opcode names from '%s'", Opcodes.class),
                exception
            );
        }

    }
}
