package org.eolang.jeo;

public final class B {
    private final A a;
    private int x = 2;

    B(A a) {
        this.a = a;
    }

    int bar() {
        return a.foo() + x;
    }
}
