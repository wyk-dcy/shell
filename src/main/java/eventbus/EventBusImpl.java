package eventbus;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.Iterator;
import java.util.concurrent.Executor;

/**
 * Dispatches events to listeners, and provides ways for listeners to register themselves.
 *
 * @author wuyongkang
 */
public class EventBusImpl implements EventBus {
    static final String DEFAULT_SUBJECT = "event.subject.default";
    private final String name;
    private final HybridExecutor executor;
    private final SubscriberExceptionHandler exceptionHandler;

    private final ObserverRegistry observers = new ObserverRegistry();
    private final SubscriberRegistry subscribers = new SubscriberRegistry(this);
    private final AbstractDispatcher dispatcher;

    public EventBusImpl(Executor executor) {
        this("default", executor);
    }

    public EventBusImpl(String name, Executor executor) {
        this(name,
                executor,
                AbstractDispatcher.hybrid(),
                LoggingHandler.INSTANCE);
    }

    public EventBusImpl(Executor executor, SubscriberExceptionHandler exceptionHandler) {
        this("default",
                executor,
                AbstractDispatcher.hybrid(),
                exceptionHandler);
    }

    public EventBusImpl(
            String name,
            Executor executor,
            AbstractDispatcher dispatcher,
            SubscriberExceptionHandler exceptionHandler) {
        this.name = Preconditions.checkNotNull(name);
        this.executor = new HybridExecutor(executor);
        this.dispatcher = Preconditions.checkNotNull(dispatcher);
        this.exceptionHandler = Preconditions.checkNotNull(exceptionHandler);
    }

    /**
     * Returns the identifier for this event bus.
     *
     * @return the identifier for this event bus
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the default executor this event bus uses for dispatching events to subscribers.
     */
    final HybridExecutor executor() {
        return executor;
    }

    /**
     * Handles the given exception thrown by a subscriber with the given context.
     */
    void handleSubscriberException(Throwable e, SubscriberExceptionContext context) {
        Preconditions.checkNotNull(e);
        Preconditions.checkNotNull(context);
        try {
            exceptionHandler.handleException(e, context);
        } catch (Throwable e2) {
        }
    }

    /**
     * Registers all subscriber methods on {@code object} to receive events.
     *
     * @param object object whose subscriber methods should be registered.
     */
    @Override
    public void register(Object object) {
        subscribers.register(object);
    }

    @Override
    public <T extends EventListener> void register(Class<T> type, T listener) {
        this.register(type, listener, Condition.ALWAYS);
    }

    @Override
    public <T extends EventListener> void register(Class<T> type, T listener, Condition condition) {
        Observer<T> observer = new Observer<>(type, listener, condition);
        this.observers.register(type, observer);
        this.register(observer);
    }

    /**
     * Unregisters all subscriber methods on a registered {@code object}.
     *
     * @param object object whose subscriber methods should be unregistered.
     * @throws IllegalArgumentException if the object was not previously registered.
     */
    @Override
    public void unregister(Object object) {
        subscribers.unregister(object);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends EventListener> void unregister(Class<T> type, T listener) {
        Observer<?> observer = this.observers.lookupObserver(type, listener);
        if (observer == null){
            return;
        }
        this.unregister(observer);
        this.observers.unregister(type, (Observer<T>) observer);
    }

    @Override
    public void post(String subject, Object event, boolean sync) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(subject), "Subject is required.");
        Iterator<Subscriber> eventSubscribers = subscribers.getSubscribers(subject, event);
        if (eventSubscribers.hasNext()) {
            dispatcher.dispatch(event, eventSubscribers, sync);
        } else if (!(event instanceof DeadEvent)) {
            // the event had no subscribers and was not itself a DeadEvent
            post(new DeadEvent(this, event), sync);
        }
    }

    @Override
    public <T extends EventListener> void post(Class<T> type, boolean sync, NotificationStrategy<T> strategy) {
        post(Observer.SUBJECT, strategy, sync);
    }

    @Override
    public <T extends EventListener> void post(Class<T> type, NotificationStrategy<T> strategy) {
        post(Observer.SUBJECT, strategy, true);
    }

    @Override
    public void post(String subject, Object event) {
        this.post(subject, event, true);
    }

    @Override
    public void post(Object event, boolean sync) {
        this.post(DEFAULT_SUBJECT, event, sync);
    }

    /**
     * Posts an event to all registered subscribers. This method will return successfully after the
     * event has been posted to all subscribers, and regardless of any exceptions thrown by
     * subscribers.
     * <p>If no subscribers have been subscribed for {@code event}'s class, and {@code event} is not
     * already a {@link DeadEvent}, it will be wrapped in a DeadEvent and reposted.
     *
     * @param event event to post.
     */
    @Override
    public void post(Object event) {
        this.post(DEFAULT_SUBJECT, event, true);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(name).toString();
    }

    /**
     * Simple logging handler for subscriber exceptions.
     */
    static final class LoggingHandler implements SubscriberExceptionHandler {
        static final LoggingHandler INSTANCE = new LoggingHandler();

        private static String message(SubscriberExceptionContext context) {
            Method method = context.getSubscriberMethod();
            return "Exception thrown by subscriber method "
                    + method.getName()
                    + '('
                    + method.getParameterTypes()[0].getName()
                    + ')'
                    + " on subscriber "
                    + context.getSubscriber()
                    + " when dispatching event: "
                    + context.getEvent();
        }

        @Override
        public void handleException(Throwable exception, SubscriberExceptionContext context) {

        }
    }
}
