package org.eolang.jeo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Boosts implements Boost {
    private final Collection<? extends Boost> all;

    public Boosts(Boost... arr) {
        this(Arrays.asList(arr));
    }

    public Boosts(final Collection<? extends Boost> boost) {
        this.all = boost;
    }

    @Override
    public Collection<IR> apply(final Collection<IR> representations) {
        if (this.all.isEmpty()) {
            return Collections.emptyList();
        }
        Collection<IR> res = representations;
        for (final Boost current : this.all) {
            res = current.apply(res);
        }
        return res;
    }
}
