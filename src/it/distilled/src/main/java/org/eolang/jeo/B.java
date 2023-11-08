package org.eolang.jeo;

public final class B {
    private final A a;
    private int x;

    B(A a) {
        this.a = a;
    }

    int bar() {
        x = 2;
        return a.foo() + x;
    }
}
