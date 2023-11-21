package org.eolang.jeo;

import java.util.HashMap;
import java.util.Map;

public class Details {

    private static final String NAME = "name";
    private static final String SOURCE = "source";
    private final Map<String, String> storage;

    public Details(final String name) {
        this(name, "Unknown");
    }

    public Details(final String name, final String source) {
        this(Details.NAME, name, Details.SOURCE, source);
    }

    public Details(final String... inits) {
        this(Details.initial(inits));
    }

    public Details(final Map<String, String> storage) {
        this.storage = storage;
    }

    /**
     * Name of the class or an object.
     * @return Name.
     */
    public String name() {
        return this.storage.get(Details.NAME);
    }

    /**
     * Original source of the representation.
     * It could be a file name or a URL.
     * @return Source.
     */
    public String source() {
        return this.storage.get(Details.SOURCE);
    }

    private static Map<String, String> initial(String... pairs) {
        final int length = pairs.length;
        if (length % 2 == 1) {
            throw new IllegalArgumentException("Must have an even number of arguments");
        }
        Map<String, String> map = new HashMap<>(pairs.length / 2);
        for (int i = 0; i < length; i += 2) {
            map.put(pairs[i], pairs[i + 1]);
        }
        return map;
    }


}
