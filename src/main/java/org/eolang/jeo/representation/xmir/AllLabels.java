package org.eolang.jeo.representation.xmir;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.objectweb.asm.Label;

public class AllLabels {

    private static final Map<String, Label> COMMON = new ConcurrentHashMap<>(0);

    private final Map<String, Label> labels;

    public AllLabels() {
        this(AllLabels.COMMON);
    }

    private AllLabels(final Map<String, Label> labels) {
        this.labels = labels;
    }

    public Label label(final String uid) {
        return this.labels.computeIfAbsent(this.clean(uid), id -> new Label());
    }

    private String clean(final String uid) {
        return uid.strip().replace("\n", "");
    }
}
