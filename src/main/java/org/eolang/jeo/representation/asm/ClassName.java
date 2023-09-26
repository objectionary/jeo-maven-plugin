package org.eolang.jeo.representation.asm;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Class name.
 * @since 0.1.0
 */
public final class ClassName extends ClassVisitor {

    /**
     * Atomic reference to store class name.
     */
    private final AtomicReference<String> bag;

    /**
     * Constructor.
     */
    public ClassName() {
        this(new AtomicReference<>());
    }

    /**
     * Constructor.
     * @param bag Atomic reference to store class name.
     */
    ClassName(final AtomicReference<String> bag) {
        this(Opcodes.ASM9, bag);
    }

    /**
     * Constructor.
     * @param api ASM API version.
     * @param bag Atomic reference to store class name.
     */
    private ClassName(final int api, final AtomicReference<String> bag) {
        super(api);
        this.bag = bag;
    }

    @Override
    public void visit(
        final int version,
        final int access,
        final String name,
        final String signature,
        final String supername,
        final String[] interfaces
    ) {
        this.bag.set(name.replace('/', '.'));
        super.visit(version, access, name, signature, supername, interfaces);
    }

    /**
     * Get class name.
     * @return Class name.
     */
    public String asString() {
        final String last = this.bag.get();
        if (Objects.isNull(last)) {
            throw new IllegalStateException(
                "Class name is not set, bug is empty. Use #visit() method to set it."
            );
        }
        return last;
    }
}
