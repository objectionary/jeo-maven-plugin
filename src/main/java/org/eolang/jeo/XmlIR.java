package org.eolang.jeo;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;

/**
 * Intermediate representation of a class files from XMIR.
 *
 * @since 0.1.0
 */
public class XmlIR implements IR {

    private final XML xml;

    public XmlIR() {
        this(new XMLDocument("<test/>"));
    }

    private XmlIR(final XML xml) {
        this.xml = xml;
    }

    @Override
    public XML toEO() {
        return this.xml;
    }

    @Override
    public byte[] toBytecode() {
        return new byte[0];
    }
}
