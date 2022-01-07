package eventbus;

import java.util.EventListener;

/**
 * Notification Strategy
 *
 * @author wuyongkang
 */
public interface NotificationStrategy<T extends EventListener> {
    /**
     * Called by event source
     *
     * @param listener listener to notify
     */
    void notify(T listener);
}
