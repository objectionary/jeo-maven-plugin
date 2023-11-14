package org.eolang.jeo.improvement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.eolang.jeo.representation.xmir.XmlBytecodeEntry;
import org.eolang.jeo.representation.xmir.XmlClass;
import org.eolang.jeo.representation.xmir.XmlMethod;
import org.objectweb.asm.Type;

/**
 * This class tries to combine constructors of the decorated class and the decorator.
 * @since 0.1
 */
public class DecoratorConstructors {

    /**
     * Decorated class.
     */
    private final XmlClass decorated;

    /**
     * Class that decorates {@link #decorated}.
     */
    private final XmlClass decorator;

    /**
     * Constructor.
     * @param decorated Decorated class.
     * @param decorator Class that decorates {@link #decorated}.
     */
    public DecoratorConstructors(
        final XmlClass decorated,
        final XmlClass decorator
    ) {
        this.decorated = decorated;
        this.decorator = decorator;
    }

    /**
     * Returns a list of constructors of the decorator class.
     * @return List of constructors.
     */
    public List<XmlMethod> constructors() {
        final List<XmlMethod> alldecorated = this.decorated.constructors();
        final List<XmlMethod> alldecoratee = this.decorator.constructors();
        final List<XmlMethod> result = new ArrayList<>(alldecorated.size() + alldecoratee.size());
        for (final XmlMethod origin : alldecorated) {
            for (final XmlMethod upper : alldecoratee) {
                final List<XmlBytecodeEntry> top = origin.instructions();
                final List<XmlBytecodeEntry> bottom = upper.instructions();
                result.add(
                    new XmlMethod(
                        upper.name(),
                        upper.access(),
                        this.combineDescriptors(
                            origin.descriptor(),
                            upper.descriptor()
                        ),
                        DecoratorConstructors.combineInstructions(origin.instructions(),
                            upper.instructions()
                        )
                    )
                );
            }
        }
        return result;
    }

    private String combineDescriptors(
        final String decorated, final String decorator
    ) {
        final String aim = this.decorated.name();
        final Type[] replacement = Type.getType(decorated).getArgumentTypes();
        final Type[] array = Arrays.stream(Type.getType(decorator).getArgumentTypes())
            .flatMap(type -> {
                if (type.getClassName().equals(aim)) {
                    return Arrays.stream(replacement);
                } else {
                    return Stream.of(type);
                }
            }).toArray(Type[]::new);
        return Type.getMethodType(Type.VOID_TYPE, array).getDescriptor();
    }

    private static XmlBytecodeEntry[] combineInstructions(
        final List<XmlBytecodeEntry> top,
        final List<XmlBytecodeEntry> bottom
    ) {
        return Collections.emptyList().toArray(new XmlBytecodeEntry[0]);
    }

}
