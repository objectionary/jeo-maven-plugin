package org.eolang.jeo.representation.asm;

import com.jcabi.xml.XMLDocument;
import org.w3c.dom.Node;

public class XmlClassProperties {
    private final XMLDocument clazz;

    public XmlClassProperties(final Node clazz) {
        this.clazz = new XMLDocument(clazz);
    }

    public int access() {
        return new HexString(this.clazz.xpath("//o[@name='access']/text()").get(0)).decodeAsInt();
    }

    public String signature() {
        return this.clazz.xpath("//o[@name='signature']/text()").get(0);
    }

    public String supername() {
        return this.clazz.xpath("//o[@name='supername']/text()").get(0);

    }


}

