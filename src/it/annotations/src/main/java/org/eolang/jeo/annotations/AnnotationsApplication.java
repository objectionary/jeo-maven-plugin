package org.eolang.jeo.annotations;

import java.util.Arrays;

@JeoAnnotation(name = "example", value = 496, color = MyEnum.GREEN, tags = {"tag1", "tag2"})
public class AnnotationsApplication {

    public static void main(String[] args) {
        Class<AnnotationsApplication> clazz = AnnotationsApplication.class;
        if (clazz.isAnnotationPresent(JeoAnnotation.class)) {
            JeoAnnotation annotation = clazz.getAnnotation(JeoAnnotation.class);
            String name = annotation.name();
            int value = annotation.value();
            MyEnum color = annotation.color();
            String[] tags = annotation.tags();
            if (!name.equals("example")) {
                throw new IllegalStateException("name is not 'example'");
            }
            if (value != 496) {
                throw new IllegalStateException("value is not 496");
            }
            if (color != MyEnum.GREEN) {
                throw new IllegalStateException("color is not GREEN");
            }
            if (!Arrays.equals(tags, new String[]{"tag1", "tag2"})) {
                throw new IllegalStateException("tags are not ['tag1', 'tag2']");
            }
            System.out.println("Annotations test passed successfully!");
        } else {
            throw new IllegalStateException(
                "JeoAnnotation not present on class AnnotationsApplication"
            );
        }
    }
}
