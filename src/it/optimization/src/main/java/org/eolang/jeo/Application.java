package org.eolang.jeo;

public class Application {
    public static void main(String[] args) {
        if (bar(1.0d) > 7) {
            System.out.println("Hello, World!");
        } else {
            System.out.println("Wake up, Neo...");
        }
    }

    private static int bar(double x) {
        if (x > 0.0d) {
            return 5;
        }
        return 8;
    }
}
