package eventbus;

import java.lang.annotation.*;

/**
 * Marks an event subscriber method as being thread-safe. This annotation indicates that EventBus
 * may invoke the event subscriber simultaneously from multiple threads.
 * <p>This does not mark the method, and so should be used in combination with {@link Subscribe}.
 *
 * @author wuyongkang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Inherited
public @interface AllowConcurrentEvents {
}
