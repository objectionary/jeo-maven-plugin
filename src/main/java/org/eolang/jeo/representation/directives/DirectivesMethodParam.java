package org.eolang.jeo.representation.directives;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

public final class DirectivesMethodParam implements Iterable<Directive> {
    /**
     * Base64 decoder.
     */
    private static final Base64.Encoder ENCODER = Base64.getEncoder();


    /**
     * Index of the parameter.
     */
    private final int index;

    /**
     * Type of the parameter.
     */
    private final Type type;

    /**
     * Annotations of the parameter.
     */
    private final List<? extends Iterable<Directive>> annotations;

    /**
     * Constructor.
     * @param index Index of the parameter.
     * @param type Type of the parameter.
     * @param annotations Annotations of the parameter.
     */
    public DirectivesMethodParam(
        final int index,
        final Type type,
        final List<? extends Iterable<Directive>> annotations
    ) {
        this.index = index;
        this.type = type;
        this.annotations = annotations;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new Directives().add("o")
            .attr("base", "param")
            .attr("line", new Random().nextInt(Integer.MAX_VALUE))
            .attr(
                "name",
                String.format(
                    "param-%s-%d",
                    DirectivesMethodParam.ENCODER.encodeToString(
                        this.type.toString().getBytes(StandardCharsets.UTF_8)
                    ),
                    this.index
                )
            )
            .append(
                this.annotations.stream()
                    .map(Directives::new)
                    .reduce(new Directives(), Directives::append)
            )
            .up()
            .iterator();
    }
}
