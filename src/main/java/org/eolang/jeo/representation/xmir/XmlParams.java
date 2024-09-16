package org.eolang.jeo.representation.xmir;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameters;

public final class XmlParams {

    private final XmlNode node;

    public XmlParams(final XmlNode node) {
        this.node = node;
    }

    public BytecodeMethodParameters params() {
        final AtomicInteger index = new AtomicInteger(0);
        return new BytecodeMethodParameters(
            this.node.children()
                .filter(
                    element -> element.hasAttribute("base", "param")
                        && !element.hasAttribute("name", "maxs")
                )
                .map(element -> new XmlParam(index.getAndIncrement(), element))
                .collect(Collectors.toMap(XmlParam::index, XmlParam::annotations))
        );
    }
}
