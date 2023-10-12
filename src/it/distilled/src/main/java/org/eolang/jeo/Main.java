package org.eolang.jeo;

public class Main {
    public static void main(String[] args) {
        int x = new B(new A(28)).bar();
        System.out.println("Result: " + x);
    }
}
