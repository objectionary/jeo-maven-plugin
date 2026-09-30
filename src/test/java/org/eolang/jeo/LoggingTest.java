/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.eolang.jeo.representation.Counter;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link Logging}.
 *
 * @since 0.18.0
 */
final class LoggingTest {

    @Test
    void printsProgressInOrderAcrossWorkers(@TempDir final Path temp) {
        new Logging(
            "Warming", "warmed", new FakeTransformation(temp.resolve("warm.xmir")), false, new Counter(1L)
        ).transform();
        final Counter counter = new Counter(500L);
        final StringWriter logs = new StringWriter();
        final Appender appender = new WriterAppender(new SimpleLayout(), logs);
        final Logger logger = Logger.getLogger(Logging.class);
        logger.addAppender(appender);
        try {
            IntStream.range(0, 500).parallel().forEach(
                index -> new Logging(
                    "Testing",
                    "tested",
                    new FakeTransformation(temp.resolve(String.format("%d.xmir", index))),
                    false,
                    counter
                ).transform()
            );
        } finally {
            logger.removeAppender(appender);
        }
        final List<Integer> printed = new ArrayList<>(500);
        final Matcher matcher = Pattern.compile("(\\d+)/500").matcher(logs.toString());
        while (matcher.find()) {
            printed.add(Integer.parseInt(matcher.group(1)));
        }
        MatcherAssert.assertThat(
            "Progress numbers of workers sharing one counter must be printed in order",
            printed,
            Matchers.equalTo(IntStream.rangeClosed(1, 500).boxed().collect(Collectors.toList()))
        );
    }
}
