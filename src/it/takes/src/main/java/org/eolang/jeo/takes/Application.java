package org.eolang.jeo.takes;

import org.takes.Request;
import org.takes.Response;
import org.takes.facets.fork.Fork;
import org.takes.http.Exit;
import org.takes.http.FtBasic;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import java.io.IOException;
import org.takes.misc.Opt;

public class Application {
    public static void main(String[] args) throws IOException {
        new FtBasic(
            new TkFork(
                new RequestCounter(
                    new TimeLog(
                        new FkRegex("/", "It is the Takes Framework"))
                )
            ),
            8080
        ).start(Exit.NEVER);
    }

    private static class TimeLog implements Fork {

        private final Fork origin;

        TimeLog(final Fork origin) {
            this.origin = origin;
        }

        @Override
        public Opt<Response> route(final Request req) throws Exception {
            System.out.printf("Request time: %d%n", System.currentTimeMillis());
            return this.origin.route(req);
        }
    }

    private static class RequestCounter implements Fork {

        private final Fork origin;
        private int counter = 0;

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
