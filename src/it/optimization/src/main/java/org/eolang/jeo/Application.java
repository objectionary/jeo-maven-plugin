package org.eolang.jeo;

public class Application {
    public static void main(String[] args) {
        final Message message;
        if (bar(1.0d) < 7) {
            message = new HelloWorld();
        } else {
            message = new WakeUpNeo();
        }
        System.out.println(message.msg());
    }

    private static int bar(double x) {
        if (x > 0.0d) {
            return 5;
        }
        return 8;
    }
}
