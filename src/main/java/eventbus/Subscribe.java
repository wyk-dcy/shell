package eventbus;

import java.lang.annotation.*;

/**
 * Marks a method as an event subscriber.
 *
 * @author wuyongkang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Inherited
public @interface Subscribe {
}
