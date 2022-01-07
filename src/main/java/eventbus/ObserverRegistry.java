package eventbus;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Broadcaster Registry
 *
 * @author wuyongkang
 */
final class ObserverRegistry {
    private final ConcurrentMap<Class<? extends EventListener>, List<Observer<?>>> subscribers =
            Maps.newConcurrentMap();

    public <T extends EventListener> void register(Class<T> type, Observer<T> observer) {
        List<Observer<?>> observers = subscribers.getOrDefault(type, null);
        if (observers == null) {
            observers = new ArrayList<>();
            subscribers.put(type, observers);
        }

        observers.add(observer);
    }

    public <T extends EventListener> void unregister(Class<T> type, Observer<T> observer) {
        List<Observer<?>> observers = subscribers.getOrDefault(type, null);
        if (observers != null) {
            boolean removed = observers.remove(observer);
            if (removed && observers.isEmpty()) {
                subscribers.remove(type);
            }
        }
    }

    public <T extends EventListener> Observer<?> lookupObserver(Class<T> type, T listener) {
        List<Observer<?>> observers = subscribers.getOrDefault(type, null);
        if (observers == null) {
            return null;
        }

        for (Observer<?> observer : observers) {
            if (observer.getListener() == listener) {
                return observer;
            }
        }

        return null;
    }
}
