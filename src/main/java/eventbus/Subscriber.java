package eventbus;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.j2objc.annotations.Weak;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A subscriber method on a specific object, plus the executor that should be used for dispatching
 * events to it.
 */
class Subscriber {
    /**
     * The object with the subscriber method.
     */
    @VisibleForTesting
    final Object target;
    /**
     * The subject for the subscriber
     */
    private final String subject;
    /**
     * Subscriber method.
     */
    private final Method method;
    /**
     * The event bus this subscriber belongs to.
     */
    @Weak
    private EventBusImpl bus;

    private Subscriber(EventBusImpl bus, Object target, Method method) {
        this.bus = bus;
        this.target = Preconditions.checkNotNull(target);
        this.method = method;
        this.subject = getSubject(method);
        method.setAccessible(true);
    }

    /**
     * Creates a {@code Subscriber} for {@code method} on {@code listener}.
     */
    static Subscriber create(EventBusImpl bus, Object listener, Method method) {
        String subject = "";
        method.isAnnotationPresent(Subject.class);
        return isDeclaredThreadSafe(method)
                ? new Subscriber(bus, listener, method)
                : new SynchronizedSubscriber(bus, listener, method);
    }

    /**
     * Checks whether {@code method} is thread-safe, as indicated by the presence of the {@link
     * AllowConcurrentEvents} or {@link ParSubscribe} annotation.
     */
    private static boolean isDeclaredThreadSafe(Method method) {
        return method.getAnnotation(AllowConcurrentEvents.class) != null
                || method.getAnnotation(ParSubscribe.class) != null;
    }

    /**
     * Return the subject for subscriber
     *
     * @param method the subscriber method
     * @return the subject for subscriber
     */
    private static String getSubject(Method method) {
        String result = EventBusImpl.DEFAULT_SUBJECT;
        if (!method.isAnnotationPresent(Subject.class)) {
            return result;
        }

        Subject subject = method.getDeclaredAnnotation(Subject.class);
        if (subject != null) {
            result = subject.value();
        }

        return Strings.isNullOrEmpty(result) ? EventBusImpl.DEFAULT_SUBJECT : result;
    }

    /**
     * Dispatches {@code event} to this subscriber using the proper executor.
     */
    final void dispatchEvent(final Object event, boolean sync) {
        HybridExecutor executor = bus.executor();
        executor.execute(
                () -> {
                    try {
                        invokeSubscriberMethod(event);
                    } catch (InvocationTargetException e) {
                        bus.handleSubscriberException(e.getCause(), context(event));
                    }
                }, sync);
    }

    /**
     * Invokes the subscriber method. This method can be overridden to make the invocation
     * synchronized.
     */
    @VisibleForTesting
    void invokeSubscriberMethod(Object event) throws InvocationTargetException {
        try {
            method.invoke(target, Preconditions.checkNotNull(event));
        } catch (IllegalArgumentException e) {
            throw new Error("Method rejected target/argument: " + event, e);
        } catch (IllegalAccessException e) {
            throw new Error("Method became inaccessible: " + event, e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Error) {
                throw (Error) e.getCause();
            }
            throw e;
        }
    }

    /**
     * Gets the context for the given event.
     */
    private SubscriberExceptionContext context(Object event) {
        return new SubscriberExceptionContext(bus, event, target, method);
    }

    /**
     * Return the subject for subscriber
     *
     * @return the subject for subscriber
     */
    public String getSubject() {
        return subject;
    }

    @Override
    public final int hashCode() {
        return (31 + method.hashCode()) * 31 + System.identityHashCode(target);
    }

    @Override
    public final boolean equals(@Nullable Object obj) {
        if (obj instanceof Subscriber) {
            Subscriber that = (Subscriber) obj;
            // Use == so that different equal instances will still receive events.
            // We only guard against the case that the same object is registered
            // multiple times
            return target == that.target && method.equals(that.method);
        }
        return false;
    }

    /**
     * Subscriber that synchronizes invocations of a method to ensure that only one thread may enter
     * the method at a time.
     */
    @VisibleForTesting
    static final class SynchronizedSubscriber extends Subscriber {

        private SynchronizedSubscriber(EventBusImpl bus, Object target, Method method) {
            super(bus, target, method);
        }

        @Override
        void invokeSubscriberMethod(Object event) throws InvocationTargetException {
            synchronized (this) {
                super.invokeSubscriberMethod(event);
            }
        }
    }
}
