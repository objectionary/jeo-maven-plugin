package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;

public final class XmlParam {

    private final int position;
    private final XmlNode root;

    public XmlParam(final int position, final XmlNode root) {
        this.position = position;
        this.root = root;
    }

    public int index() {
        return this.position;
    }

    public List<BytecodeAnnotation> annotations() {
        return this.root.children().filter(node -> node.hasAttribute("base", "annotation"))
            .map(XmlAnnotation::new)
            .map(XmlAnnotation::toBytecode)
            .collect(Collectors.toList());
    }

}
