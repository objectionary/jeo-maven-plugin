/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo.representation.xmir;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.objectweb.asm.Label;

/**
 * All the labels that have been found in bytecode.
 * @since 0.1
 */
public final class AllLabels {

    /**
     * The common cache for all the labels.
     * @todo #241:90min Remove Single Label Cache.
     *  The proper solution would be to use single {@link AllLabels} instance without any shared
     *  cache. However, it would require to change the whole architecture of the
     *  {@link org.eolang.jeo.representation.xmir} package and it's why it's not done yet.
     *  The current solution is a temporary workaround. So, in this task we have to:
     *  - Use single instance of the {@link AllLabels} class.
     *  - Pass that instance to all the classes that need it from top to bottom.
     *  - Remove the {@link AllLabels#CACHE} field.
     *  But this issue isn't urgent and can be done later.
     */
    private static final Map<String, Label> CACHE = new ConcurrentHashMap<>(0);

    /**
     * All the labels.
     */
    private final Map<String, Label> labels;

    /**
     * Constructor.
     */
    public AllLabels() {
        this(AllLabels.CACHE);
    }

    /**
     * Constructor.
     * @param labels All the labels.
     */
    private AllLabels(final Map<String, Label> labels) {
        this.labels = labels;
    }

    /**
     * Get label by UID.
     * @param uid UID.
     * @return Label.
     */
    public Label label(final String uid) {
        return this.labels.computeIfAbsent(AllLabels.clean(uid), id -> new Label());
    }

    /**
     * Find UID of Label.
     * @param label Label.
     * @return UID.
     */
    public String uid(final Label label) {
        return this.labels.entrySet().stream()
            .filter(e -> e.getValue().equals(label))
            .findFirst().orElseGet(
                () -> {
                    final String generated = UUID.randomUUID().toString();
                    this.labels.put(generated, label);
                    return Map.entry(generated, label);
                }
            ).getKey();
    }

    /**
     * Clean the UID.
     * @param uid UID with new lines and spaces.
     * @return Cleaned UID.
     */
    private static String clean(final String uid) {
        return uid.strip().replace("\n", "");
    }
}
