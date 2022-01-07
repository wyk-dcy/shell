package eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wuyongkang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Subscribe
@AllowConcurrentEvents
public @interface ParSubscribe {
}
