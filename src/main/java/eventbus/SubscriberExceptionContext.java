package eventbus;

import com.google.common.base.Preconditions;

import java.lang.reflect.Method;

/**
 * Context for an exception thrown by a subscriber.
 *
 * @author wuyongkang
 */
public class SubscriberExceptionContext {
    private final EventBusImpl eventBus;
    private final Object event;
    private final Object subscriber;
    private final Method subscriberMethod;

    /**
     * @param eventBus         The {@link EventBusImpl} that handled the event and the subscriber. Useful for
     *                         broadcasting a a new event based on the error.
     * @param event            The event object that caused the subscriber to throw.
     * @param subscriber       The source subscriber context.
     * @param subscriberMethod the subscribed method.
     */
    SubscriberExceptionContext(
            EventBusImpl eventBus, Object event, Object subscriber, Method subscriberMethod) {
        this.eventBus = Preconditions.checkNotNull(eventBus);
        this.event = Preconditions.checkNotNull(event);
        this.subscriber = Preconditions.checkNotNull(subscriber);
        this.subscriberMethod = Preconditions.checkNotNull(subscriberMethod);
    }

    /**
     * @return The {@link EventBusImpl} that handled the event and the subscriber. Useful for broadcasting
     * a a new event based on the error.
     */
    public EventBusImpl getEventBus() {
        return eventBus;
    }

    /**
     * @return The event object that caused the subscriber to throw.
     */
    public Object getEvent() {
        return event;
    }

    /**
     * @return The object context that the subscriber was called on.
     */
    public Object getSubscriber() {
        return subscriber;
    }

    /**
     * @return The subscribed method that threw the exception.
     */
    public Method getSubscriberMethod() {
        return subscriberMethod;
    }
}
