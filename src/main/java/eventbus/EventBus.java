package eventbus;

import java.util.EventListener;

/**
 * Dispatches events to listeners, and provides ways for listeners to register themselves
 *
 * @author wuyongkang
 */
public interface EventBus {
    /**
     * Registers the listener
     *
     * @param object the listener instance
     */
    void register(Object object);

    /**
     * Registers the listener
     *
     * @param type     the listener type
     * @param listener the listener to register
     * @param <T>      the listener class type
     */
    <T extends EventListener> void register(Class<T> type, T listener);

    /**
     * Registers the listener
     *
     * @param type      the listener type
     * @param listener  the listener to register
     * @param condition the condition for notification
     * @param <T>       the listener class type
     */
    <T extends EventListener> void register(Class<T> type, T listener, Condition condition);

    /**
     * Unregisters the listener
     *
     * @param object the listener instance
     */
    void unregister(Object object);

    /**
     * Unregisters the listener
     *
     * @param type     the listener type
     * @param listener the listener to register
     * @param <T>      the listener class type
     */
    <T extends EventListener> void unregister(Class<T> type, T listener);

    /**
     * Post event to the listeners by sync or async mode
     *
     * @param subject event message
     * @param event   event message
     * @param sync    a {@code true} represents sync mode, otherwise async mode
     */
    void post(String subject, Object event, boolean sync);

    /**
     * Post event to the listeners by sync or async mode
     *
     * @param type     listener type
     * @param sync     a {@code true} represents sync mode, otherwise async mode
     * @param strategy notification strategy
     * @param <T>the   listener class type
     */
    <T extends EventListener> void post(Class<T> type, boolean sync, NotificationStrategy<T> strategy);

    /**
     * Post event to the listeners by sync mode
     *
     * @param type     listener type
     * @param strategy notification strategy
     * @param <T>the   listener class type
     */
    <T extends EventListener> void post(Class<T> type, NotificationStrategy<T> strategy);

    /**
     * Post event to the listeners by sync or async mode
     *
     * @param subject event message
     * @param event   event message
     */
    void post(String subject, Object event);

    /**
     * Post event to the listeners by sync or async mode
     *
     * @param event event message
     * @param sync  a {@code true} represents sync mode, otherwise async mode
     */
    void post(Object event, boolean sync);

    /**
     * Post event to the listeners by sync mode
     *
     * @param event event message
     */
    void post(Object event);
}
