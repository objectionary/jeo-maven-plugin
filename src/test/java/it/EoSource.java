package it;

import com.jcabi.xml.XML;
import org.cactoos.io.ResourceOf;
import org.eolang.parser.Syntax;

public class EoSource {

    private final String resource;

    public EoSource(final String resource) {
        this.resource = resource;
    }

    XML parse() {
        final ResourceOf eo = new ResourceOf(this.resource);
        final XMLOutput output = new XMLOutput();
        new Syntax("scenario", eo, output);
        return output.xml();
    }


}
