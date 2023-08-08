package org.eolang.jeo;

import java.util.Collection;

public interface Boost {

    Collection<IR> apply(Collection<? extends IR> representations);

}
