package eventbus;

import java.util.EventListener;

/**
 * Notification Observer
 *
 * @author wuyongkang
 */
final class Observer<T extends EventListener> {
    static final String SUBJECT = "cn.elibot.robot.eventbus.__notification__";

    private final Class<T> type;
    private final Object listener;
    private final Condition when;

    public Observer(Class<T> type, Object listener, Condition when) {
        this.type = type;
        this.listener = listener;
        this.when = when;
    }

    @ParSubscribe
    @Subject(SUBJECT)
    @SuppressWarnings("unchecked")
    public void onReceive(NotificationStrategy<T> strategy) {
        if (this.when.shouldNotify()) {
            strategy.notify((T) listener);
        }
    }

    public Class<T> getType() {
        return type;
    }

    public Object getListener() {
        return listener;
    }
}
