package it;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.cactoos.Output;

public class XMLOutput implements Output {

    final ByteArrayOutputStream baos;

    public XMLOutput() {
        this(new ByteArrayOutputStream());
    }

    private XMLOutput(final ByteArrayOutputStream baos) {
        this.baos = baos;
    }


    @Override
    public OutputStream stream() {
        return this.baos;
    }

    public XML xml() {
        return new XMLDocument(this.baos.toByteArray());
    }
}
