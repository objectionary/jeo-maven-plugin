package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.objectweb.asm.Handle;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives Handle.
 * Xmir representation of the Java ASM Handle object.
 * @since 0.1
 * @todo #329:30min Implement DirectivesHandle class.
 *  The {@link Handle} class is one of the parameters for INVOKEDYNAMIC instruction.
 *  The class should be implemented in the same way as {@link DirectivesLabel}.
 *  Don't forget to add tests.
 */
public class DirectivesHandle implements Iterable<Directive> {

    /**
     * ASM Handle object.
     */
    private final Handle handle;

    /**
     * Constructor.
     * @param handle ASM Handle object.
     */
    public DirectivesHandle(final Handle handle) {
        this.handle = handle;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new Directives()
            .add("o")
            .attr("base", "handle")
            .attr("name", handle.getName())
            .up()
            .iterator();
    }
}
