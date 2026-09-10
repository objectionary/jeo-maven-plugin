package org.eolang.jeo.probe;

import java.nio.file.Paths;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.junit.jupiter.api.Test;

final class Probe2Test {

    @Test
    void disassemblesTwice() {
        final java.nio.file.Path p =
            Paths.get("target/classes/org/eolang/jeo/representation/bytecode/InstructionsFlow.class");
        final String one = new BytecodeRepresentation(p).toXmir().toString();
        final String two = new BytecodeRepresentation(p).toXmir().toString();
        System.out.println("### EQUAL=" + one.equals(two));
        final String[] a = one.split("\n");
        final String[] b = two.split("\n");
        int shown = 0;
        for (int i = 0; i < Math.min(a.length, b.length) && shown < 8; ++i) {
            if (!a[i].equals(b[i])) {
                System.out.println("  - " + a[i].trim());
                System.out.println("  + " + b[i].trim());
                shown++;
            }
        }
    }
}
