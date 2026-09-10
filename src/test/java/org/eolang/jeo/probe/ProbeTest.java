/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.probe;

import com.jcabi.xml.XMLDocument;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.XmirRepresentation;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.junit.jupiter.api.Test;

final class ProbeTest {

    private static String norm(final String xml) {
        String res = xml.replaceAll("time=\"[^\"]*\"", "time=\"X\"");
        res = res.replaceAll("ms=\"[^\"]*\"", "ms=\"X\"");
        res = res.replaceAll("L[0-9]{4,}", "LX");
        res = res.replaceAll("4C-(3[0-9]-?)+", "LXHEX");
        return res;
    }

    @Test
    void roundTripsCorpus() throws Exception {
        final Path root = Paths.get(System.getProperty("corpus", "target/classes"));
        final List<Path> all;
        try (Stream<Path> walk = Files.walk(root)) {
            all = walk.filter(p -> p.toString().endsWith(".class")).collect(Collectors.toList());
        }
        int ok = 0;
        int bad = 0;
        for (final Path clazz : all) {
            try {
                final String raw = new BytecodeRepresentation(clazz).toXmir().toString();
                final String first = ProbeTest.norm(raw);
                final Bytecode back = new XmirRepresentation(new XMLDocument(raw)).toBytecode();
                final String second = ProbeTest.norm(
                    new BytecodeRepresentation(back).toXmir().toString()
                );
                if (!first.equals(second)) {
                    bad = bad + 1;
                    System.out.println("### DIFF " + clazz);
                    final String[] one = first.split("\n");
                    final String[] two = second.split("\n");
                    int shown = 0;
                    for (int i = 0; i < Math.min(one.length, two.length) && shown < 6; ++i) {
                        if (!one[i].equals(two[i])) {
                            System.out.println("  - " + one[i].trim());
                            System.out.println("  + " + two[i].trim());
                            shown = shown + 1;
                        }
                    }
                    if (one.length != two.length) {
                        System.out.println("  LINES " + one.length + " vs " + two.length);
                    }
                } else {
                    ok = ok + 1;
                }
            } catch (final RuntimeException | StackOverflowError ex) {
                bad = bad + 1;
                System.out.println("### FAIL " + clazz);
                Throwable cur = ex;
                int depth = 0;
                while (cur != null && depth < 6) {
                    String msg = String.valueOf(cur.getMessage());
                    if (msg.length() > 300) {
                        msg = msg.substring(0, 300);
                    }
                    System.out.println("    " + cur.getClass().getName() + ": " + msg);
                    cur = cur.getCause();
                    depth = depth + 1;
                }
            }
        }
        System.out.println("### TOTAL ok=" + ok + " bad=" + bad + " of " + all.size());
    }
}
