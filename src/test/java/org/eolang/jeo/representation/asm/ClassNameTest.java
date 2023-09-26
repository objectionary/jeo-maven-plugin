package org.eolang.jeo.representation.asm;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link ClassName}.
 * @since 0.1.0
 */
class ClassNameTest {

    @Test
    void retrievesClassName() {
        final ClassName name = new ClassName();
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(Opcodes.ASM9,
            Opcodes.ACC_PUBLIC,
            "representation/asm/ClassNameTest",
            null,
            "java/lang/Object",
            null
        );
        new ClassReader(writer.toByteArray()).accept(name, 0);
        MatcherAssert.assertThat(
            "Can't retrieve class name, or it's incorrect",
            name.asString(),
            Matchers.equalTo("representation.asm.ClassNameTest")
        );
    }
}