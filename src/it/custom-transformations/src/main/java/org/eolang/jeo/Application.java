package org.eolang.jeo;

import org.eolang.jeo.Message;
import org.eolang.jeo.HelloWorld;
import org.eolang.jeo.WakeUpNeo;

public class Application {
    public static void main(String[] args) {
        Message message;
        if (bar(1.0d) < 7) {
            message = new HelloWorld();
        } else {
            message = new WakeUpNeo();
        }
        long a = 0L;
        a = a + 123L;
        System.out.println(message.msg());
    }

    private static int bar(double x) {
        if (x > 0.0d) {
            return 5;
        }
        return 8;
    }
}
