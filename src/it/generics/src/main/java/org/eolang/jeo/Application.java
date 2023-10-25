package org.eolang.jeo;

public class Application {
    public static void main(String[] args) {
        new Developer(
            "Yegor",
            new Language() {
                public String name() {
                    return "EO";
                }
            }
        ).writeCode();
        new Developer(
            "Lev",
            new Language() {
                public String name() {
                    return "Rust";
                }
            }
        ).writeCode();
        new Developer(
            "Maxim",
            new Language() {
                public String name() {
                    return "PHP";
                }
            }
        ).writeCode();
        new Developer(
            "Roman",
            new Language() {
                public String name() {
                    return "Kotlin";
                }
            }
        ).writeCode();
    }
}
