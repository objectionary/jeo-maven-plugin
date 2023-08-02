package org.eolang.jeo;

import java.util.Collection;
import java.util.List;

public interface Boost {

    Collection<IR> apply(Collection<IR> bytecodeClass);

}
