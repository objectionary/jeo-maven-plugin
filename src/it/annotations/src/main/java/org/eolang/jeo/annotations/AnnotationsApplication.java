package org.eolang.jeo.annotations;

import java.util.Arrays;

/**
 * Annotations application.
 * @since 0.3
 * @todo #488:90min Add more examples of annotations usage.
 *  Currenlty we support only some annotations usage. Add more examples, especially
 *  when we use another annotations inside, or when we have more complex annotations.
 */
@JeoAnnotation(
    name = "example",
    value = 496,
    color = MyEnum.GREEN,
    tags = {"tag1", "tag2"},
    ints = {1, 2, 3},
    longs = {10L, 20L, 30L},
    doubles = {1.0, 2.0, 3.0},
    floats = {1.0f, 2.0f, 3.0f},
//    classes = {AnnotationsApplication.class},
    nested = @NestedAnnotation
//    ,
//    nestedArray = {
//        @NestedAnnotation(name = "nested1"),
//        @NestedAnnotation(name = "nested2")
//    }
)
public class AnnotationsApplication {

    public static void main(String[] args) {
        Class<AnnotationsApplication> clazz = AnnotationsApplication.class;
        if (clazz.isAnnotationPresent(JeoAnnotation.class)) {
            JeoAnnotation annotation = clazz.getAnnotation(JeoAnnotation.class);
            String name = annotation.name();
            int value = annotation.value();
            MyEnum color = annotation.color();
            String[] tags = annotation.tags();
            int[] ints = annotation.ints();
            long[] longs = annotation.longs();
            double[] doubles = annotation.doubles();
            float[] floats = annotation.floats();
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
            if (!Arrays.equals(ints, new int[]{1, 2, 3})) {
                throw new IllegalStateException("ints are not [1, 2, 3]");
            }
            if (!Arrays.equals(longs, new long[]{10L, 20L, 30L})) {
                throw new IllegalStateException("longs are not [10L, 20L, 30L]");
            }
            if (!Arrays.equals(doubles, new double[]{1.0, 2.0, 3.0})) {
                throw new IllegalStateException("doubles are not [1.0, 2.0, 3.0]");
            }
            if (!Arrays.equals(floats, new float[]{1.0f, 2.0f, 3.0f})) {
                throw new IllegalStateException("floats are not [1.0f, 2.0f, 3.0f]");
            }
//            if (!Arrays.equals(annotation.classes(), new Class<?>[]{AnnotationsApplication.class})) {
//                throw new IllegalStateException("classes are not [AnnotationsApplication.class]");
//            }
            if (annotation.nested() == null) {
                throw new IllegalStateException("nested is null");
            }
//            if (annotation.nestedArray().length != 2) {
//                throw new IllegalStateException("nestedArray length is not 2");
//            }
            System.out.println("Annotations test passed successfully!");
        } else {
            throw new IllegalStateException(
                "JeoAnnotation not present on class AnnotationsApplication"
            );
        }
    }
}
