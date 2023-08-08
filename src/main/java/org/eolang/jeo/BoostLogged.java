package org.eolang.jeo;

import com.jcabi.log.Logger;
import java.util.Collection;
import java.util.Collections;

public class BoostLogged implements Boost {
    @Override
    public Collection<IR> apply(final Collection<? extends IR> representations) {
        representations.forEach(
            ir -> Logger.info(this, "Optimization candidate: %s", ir)
        );
        return Collections.unmodifiableCollection(representations);
    }
}
