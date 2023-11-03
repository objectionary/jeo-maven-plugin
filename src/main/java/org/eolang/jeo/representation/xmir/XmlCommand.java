package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeMethod;

/**
 * XML representation of bytecode instruction or a label.
 * @todo #226:90min Rename XmlCommand to XmlInstruction.
 *  XmlCommand is a bad name for this class. It is not a command, it is an instruction in general.
 *  Since instruction could be a label or a bytecode instruction, it is better to rename this class
 *  to XmlInstruction. After that, rename all usages of the old XmlInstruction to XmlCommand.
 */
public interface XmlCommand {

    void writeTo(BytecodeMethod method);
}
