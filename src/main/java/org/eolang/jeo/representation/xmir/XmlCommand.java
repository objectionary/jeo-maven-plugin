package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeMethod;

public interface XmlCommand {

    void writeTo(BytecodeMethod method);
}
