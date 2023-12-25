package org.eolang.benchmark;

public class Main {
    public static void main(String... args) {
        App app = new App();
        long total = Long.parseLong(args[0]);
        long sum = 0L;
        for (long i = 0; i < total; ++i) {
            sum += app.run();
        }
        System.out.println(sum);
    }
}