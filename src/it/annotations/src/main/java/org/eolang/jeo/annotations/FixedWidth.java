import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is an annotation from JUnit library.
 * We use it in this test pack because it caused some errors:
 * https://github.com/objectionary/jeo-maven-plugin/issues/576
 * We need to check if the plugin can handle this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface FixedWidth {
    int value() default -1;

    char padding() default ' ';

    boolean keepPadding() default false;

    int from() default -1;

    int to() default -1;
}