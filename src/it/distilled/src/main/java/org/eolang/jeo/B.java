package org.eolang.jeo;

public final class B {
    private final A a;
    private int x;

    B(A a){ this(a, 0); }

    B(A a, int x) {
        this.a = a;
        this.x = x;
    }

    int bar() {
        x = 2;
        return a.foo() + x;
    }
}
