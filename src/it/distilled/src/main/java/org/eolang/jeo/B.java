package org.eolang.jeo;

public final class B{
    private final A a;

    B(A a){
        this.a = a;
    }

    int bar(){
        return a.foo() + 2;
    }
}
