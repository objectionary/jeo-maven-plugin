package org.eolang.jeo.improvement;

import java.util.Collections;
import java.util.List;
import org.eolang.jeo.representation.xmir.XmlClass;
import org.eolang.jeo.representation.xmir.XmlMethod;

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
        return Collections.emptyList();
    }
}
