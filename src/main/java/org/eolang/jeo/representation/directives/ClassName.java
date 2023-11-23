package org.eolang.jeo.representation.directives;

public class ClassName {

    private final static String DELIMETER = "/";
    private final String name;

    public ClassName(final String name) {
        this.name = name;
    }

    public String pckg() {
        final String result;
        final int index = this.name.lastIndexOf(ClassName.DELIMETER);
        if (index == -1) {
            result = "";
        } else {
            result = this.name.substring(0, index).replace(ClassName.DELIMETER, ".");
        }
        return result;
    }

    public String name() {
        if (this.name.contains(ClassName.DELIMETER)) {
            final String[] split = this.name.split(ClassName.DELIMETER);
            return split[split.length - 1];
        } else {
            return this.name;
        }
    }
}
