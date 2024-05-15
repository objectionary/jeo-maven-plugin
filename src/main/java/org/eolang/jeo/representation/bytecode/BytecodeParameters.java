package org.eolang.jeo.representation.bytecode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.objectweb.asm.MethodVisitor;

@ToString
@EqualsAndHashCode
public final class BytecodeParameters {

    private final Map<Integer, List<BytecodeAnnotation>> annotations;

    public BytecodeParameters() {
        this(new HashMap<>(0));
    }

    public BytecodeParameters(final Map<Integer, List<BytecodeAnnotation>> annotations) {
        this.annotations = annotations;
    }

    public void write(final MethodVisitor visitor) {
        this.annotations.forEach(
            (key, value) -> value.forEach(annotation -> annotation.write(key, visitor))
        );
    }
}
