package org.eolang.jeo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

class BytecodeIRTest {


    @Test
    @Disabled
    void parsesBytecode() throws Exception {

        final ResourceOf input = new ResourceOf("BoostLogged.class");
//        final InputStream stream = input.stream();
//        StringBuilder sb = new StringBuilder();
//        for (int ch; (ch = stream.read()) != -1; ) {
//            sb.append((char) ch);
//        }
//        System.out.println(sb.toString());
//        stream.close();

        new BytecodeIR(input).parse();
    }


    @Test
    void parseBytecode() {
        try (InputStream stream = Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("MethodByte.class")) {
            //MethodByte.class has 60 version (Java 16)
            new ClassReader(stream)
                .accept(new ClassPrinter(), 0);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private class ClassPrinter extends ClassVisitor {
        protected ClassPrinter() {
            super(Opcodes.ASM9);
        }

        @Override
        public void visit(final int version, final int access, final String name,
            final String signature, final String superName,
            final String[] interfaces
        ) {
            System.out.println("name: " + name);
            System.out.println("signature: " + signature);
            System.out.println("version: " + version);
            System.out.println("supername " + superName);
            System.out.println("interfaces: " + interfaces);
            super.visit(version, access, name, signature, superName, interfaces);
        }
    }

}