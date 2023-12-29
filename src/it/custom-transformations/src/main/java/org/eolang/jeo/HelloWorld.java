package org.eolang.jeo;

public class HelloWorld implements Message {

    @Override
    public String msg() {
        return "Hello, World!";
    }
}