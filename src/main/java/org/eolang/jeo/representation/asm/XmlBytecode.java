package org.eolang.jeo.representation.asm;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlBytecode extends ClassWriter {

    private final XML xml;

    public XmlBytecode(final XML xml) {
        super(0);
        this.xml = xml;
    }

    @Override
    public byte[] toByteArray() {
        final String name = this.xml.xpath("/program/@name").get(0);

        this.travers(this.xml.node());
        return super.toByteArray();
    }


    private void travers(Node node) {
        if (this.isClass(node)) {
            final Node name = node.getAttributes().getNamedItem("name");
            final String content = name.getTextContent();
            this.visit(
                Opcodes.ASM9,
                Opcodes.ACC_PUBLIC,
                content,
                null,
                "java/lang/Object",
                null
            );
        } else if (this.isMethod(node)) {
            this.visitMethod(Opcodes.ACC_PUBLIC, "main", "()V", null, null);
        } else {
            Logger.warn(this, String.format("Skip node: %s", node));
        }
        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            this.travers(children.item(i));
        }
    }

    private boolean isClass(final Node node) {
        return node.getNodeName().equals("o")
            && node.getParentNode().getNodeName().equals("objects");
    }

    private boolean isMethod(final Node node) {
        return node.getNodeName().equals("o")
            && !node.getParentNode().getNodeName().equals("objects");
    }

}
