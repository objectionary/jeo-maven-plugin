package org.eolang.jeo;

public class Bar {
    public int foo(int x) {
        if (x > 0) {
            return 1;
        }
        return 2;
    }

    public void sipush() {
        short s = 256;
        System.out.println(s);
    }
}