package org.eolang.jeo.representation.directives;

public class ClassName {

    private final static String DELIMETER = "/";
    private final String name;

    public ClassName(final String name) {
        this.name = name;
    }

    public String pckg() {
        return this.name.substring(0, this.name.lastIndexOf(ClassName.DELIMETER))
            .replace(ClassName.DELIMETER, ".");
    }

    public String name() {
        final String[] split = this.name.split(ClassName.DELIMETER);
        return split[split.length - 1];
    }
}
