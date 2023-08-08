package org.eolang.jeo;

import java.util.Collection;

/**
 * Optimization boost.
 * @since 0.1.0
 */
public interface Boost {

    /**
     * Apply the boost.
     * @param representations IRs to optimize.
     * @return Optimized IRs.
     */
    Collection<IR> apply(Collection<? extends IR> representations);

}
