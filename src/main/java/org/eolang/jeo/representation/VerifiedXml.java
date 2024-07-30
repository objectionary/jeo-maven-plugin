package org.eolang.jeo.representation;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.directives.DirectivesClassVisitor;
import org.eolang.parser.Schema;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

public final class VerifiedXml {

    private final DirectivesClassVisitor directives;

    public VerifiedXml(final DirectivesClassVisitor visitor) {
        this.directives = visitor;
    }

    public XML asXml() throws ImpossibleModificationException {
        final XML res = this.cleanAliases(new XMLDocument(new Xembler(this.directives).xml()));
        new Schema(res).check();
        return res;
    }

    XML cleanAliases(final XML original) {
        if (VerifiedXml.dontHaveInstructions(original)) {
            try {
                return new XMLDocument(
                    new Xembler(
                        new Directives()
                            .xpath("./program/metas/meta[head[text()='alias']]")
                            .remove()
                    ).apply(original.node())
                );
            } catch (final ImpossibleModificationException exception) {
                throw new RuntimeException(exception);
            }
        }
        return original;
    }

    private static boolean dontHaveInstructions(final XML original) {
        return original.xpath(
            ".//o[@base='seq']/o[@base='tuple']/o[@base='opcode' or @base='label']/text()"
        ).isEmpty();
    }
}
