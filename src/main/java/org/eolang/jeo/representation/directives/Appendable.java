package org.eolang.jeo.representation.directives;

import org.xembly.Directive;

public interface Appendable {


    void append(Iterable<Directive> directives);

    Iterable<Directive> sum();
}
