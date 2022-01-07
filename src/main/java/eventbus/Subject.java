package eventbus;

import java.lang.annotation.*;

/**
 * Event Subject
 *
 * @author wuyongkang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Inherited
public @interface Subject {
    String value() default "";
}
