/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
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
package org.eolang.jeo.takes;

import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.Fork;
import org.takes.http.Exit;
import org.takes.http.FtBasic;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import java.io.IOException;
import org.takes.http.FtRemote;
import org.takes.misc.Opt;
import org.takes.rq.RqFake;
import org.takes.rq.RqSimple;
import org.takes.rs.RsPrint;

/**
 * Takes Application Entry Point.
 * @since 0.1
 */
public class Application {
    public static void main(String[] args) throws Exception {
        final String path = "/";
        System.out.println(
            new RsPrint(
                new TkFork(
                    new RequestCounter(
                        new TimeLog(
                            new FkRegex(path, "It is the Takes Framework"))
                    )
                ).act(new RqFake("GET", path))).asString()
        );
    }

    /**
     * Prints the request time in the console.
     * @since 0.1
     */
    private static class TimeLog implements Fork {

        /**
         * Original fork.
         */
        private final Fork origin;

        /**
         * Constructor.
         * @param origin Original fork.
         */
        TimeLog(final Fork origin) {
            this.origin = origin;
        }

        @Override
        public Opt<Response> route(final Request req) throws Exception {
            System.out.printf("Request time: %d%n", System.currentTimeMillis());
            return this.origin.route(req);
        }
    }

    /**
     * Counts the requests.
     * @since 0.1
     */
    private static class RequestCounter implements Fork {

        /**
         * Original fork.
         */
        private final Fork origin;

        /**
         * Request counter.
         * Counts each request.
         */
        private int counter = 0;

        /**
         * Constructor.
         * @param origin Original fork.
         */
        RequestCounter(final Fork origin) {
            this.origin = origin;
        }

        @Override
        public Opt<Response> route(final Request req) throws Exception {
            this.counter += 1;
            System.out.printf("Request #%d%n", this.counter);
            return this.origin.route(req);
        }
    }
}
