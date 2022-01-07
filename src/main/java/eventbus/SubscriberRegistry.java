package eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.*;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.*;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.j2objc.annotations.Weak;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Registry of subscribers to a single event bus.
 */
final class SubscriberRegistry {

    /**
     * A thread-safe cache that contains the mapping from each class to all methods in that class and
     * all super-classes, that are annotated with {@code @Subscribe}. The cache is shared across all
     * instances of this class; this greatly improves performance if multiple EventBus instances are
     * created and objects of the same class are registered on all of them.
     */
    private static final LoadingCache<Class<?>, ImmutableList<Method>> subscriberMethodsCache =
            CacheBuilder.newBuilder()
                    .weakKeys()
                    .build(
                            new CacheLoader<Class<?>, ImmutableList<Method>>() {
                                @Override
                                public ImmutableList<Method> load(Class<?> concreteClass) throws Exception {
                                    return getAnnotatedMethodsNotCached(concreteClass);
                                }
                            });
    /**
     * Global cache of classes to their flattened hierarchy of supertypes.
     */
    private static final LoadingCache<Class<?>, ImmutableSet<Class<?>>> flattenHierarchyCache =
            CacheBuilder.newBuilder()
                    .weakKeys()
                    .build(
                            new CacheLoader<Class<?>, ImmutableSet<Class<?>>>() {
                                // <Class<?>> is actually needed to compile
                                @SuppressWarnings("RedundantTypeArguments")
                                @Override
                                public ImmutableSet<Class<?>> load(Class<?> concreteClass) {
                                    return ImmutableSet.<Class<?>>copyOf(
                                            TypeToken.of(concreteClass).getTypes().rawTypes());
                                }
                            });
    /**
     * All registered subscribers, indexed by event type.
     * <p>The {@link CopyOnWriteArraySet} values make it easy and relatively lightweight to get an
     * immutable snapshot of all current subscribers to an event without any locking.
     */
    private final ConcurrentMap<Class<?>, CopyOnWriteArraySet<Subscriber>> subscribers =
            Maps.newConcurrentMap();
    /**
     * The event bus this registry belongs to.
     */
    @Weak
    private final EventBusImpl bus;

    SubscriberRegistry(EventBusImpl bus) {
        this.bus = Preconditions.checkNotNull(bus);
    }

    private static ImmutableList<Method> getAnnotatedMethods(Class<?> clazz) {
        return subscriberMethodsCache.getUnchecked(clazz);
    }

    private static ImmutableList<Method> getAnnotatedMethodsNotCached(Class<?> clazz) {
        Set<? extends Class<?>> supertypes = TypeToken.of(clazz).getTypes().rawTypes();
        Map<MethodIdentifier, Method> identifiers = Maps.newHashMap();
        for (Class<?> supertype : supertypes) {
            for (Method method : supertype.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Subscribe.class)
                        || method.isAnnotationPresent(ParSubscribe.class)) {
                    if (!method.isSynthetic()) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        Preconditions.checkArgument(
                                parameterTypes.length == 1,
                                "Method %s has @Subscribe annotation but has %s parameters."
                                        + "Subscriber methods must have exactly 1 parameter.",
                                method,
                                parameterTypes.length);

                        MethodIdentifier ident = new MethodIdentifier(method);
                        if (!identifiers.containsKey(ident)) {
                            identifiers.put(ident, method);
                        }
                    }
                }

            }
        }
        return ImmutableList.copyOf(identifiers.values());
    }

    /**
     * Flattens a class's type hierarchy into a set of {@code Class} objects including all
     * superclasses (transitively) and all interfaces implemented by these superclasses.
     */
    @VisibleForTesting
    static ImmutableSet<Class<?>> flattenHierarchy(Class<?> concreteClass) {
        try {
            return flattenHierarchyCache.getUnchecked(concreteClass);
        } catch (UncheckedExecutionException e) {
            throw Throwables.propagate(e.getCause());
        }
    }

    /**
     * Registers all subscriber methods on the given listener object.
     */
    void register(Object listener) {
        Multimap<Class<?>, Subscriber> listenerMethods = findAllSubscribers(listener);

        for (Entry<Class<?>, Collection<Subscriber>> entry : listenerMethods.asMap().entrySet()) {
            Class<?> eventType = entry.getKey();
            Collection<Subscriber> eventMethodsInListener = entry.getValue();

            CopyOnWriteArraySet<Subscriber> eventSubscribers = subscribers.get(eventType);

            if (eventSubscribers == null) {
                CopyOnWriteArraySet<Subscriber> newSet = new CopyOnWriteArraySet<>();
                eventSubscribers =
                        MoreObjects.firstNonNull(subscribers.putIfAbsent(eventType, newSet), newSet);
            }

            eventSubscribers.addAll(eventMethodsInListener);
        }
    }

    /**
     * Unregisters all subscribers on the given listener object.
     */
    void unregister(Object listener) {
        Multimap<Class<?>, Subscriber> listenerMethods = findAllSubscribers(listener);

        for (Entry<Class<?>, Collection<Subscriber>> entry : listenerMethods.asMap().entrySet()) {
            Class<?> eventType = entry.getKey();
            Collection<Subscriber> listenerMethodsForType = entry.getValue();

            CopyOnWriteArraySet<Subscriber> currentSubscribers = subscribers.get(eventType);
            if (currentSubscribers == null || !currentSubscribers.removeAll(listenerMethodsForType)) {
                // if removeAll returns true, all we really know is that at least one subscriber was
                // removed... however, barring something very strange we can assume that if at least one
                // subscriber was removed, all subscribers on listener for that event type were... after
                // all, the definition of subscribers on a particular class is totally static
                throw new IllegalArgumentException(
                        "missing event subscriber for an annotated method. Is " + listener + " registered?");
            }

            // don't try to remove the set if it's empty; that can't be done safely without a lock
            // anyway, if the set is empty it'll just be wrapping an array of length 0
        }
    }

    @VisibleForTesting
    Set<Subscriber> getSubscribersForTesting(Class<?> eventType) {
        return MoreObjects.firstNonNull(subscribers.get(eventType), ImmutableSet.of());
    }

    /**
     * Gets an iterator representing an immutable snapshot of all subscribers to the given event at
     * the time this method is called.
     */
    Iterator<Subscriber> getSubscribers(Object event) {
        return getSubscribers(EventBusImpl.DEFAULT_SUBJECT, event);
    }

    /**
     * Gets an iterator representing an immutable snapshot of all subscribers to the given event at
     * the time this method is called.
     */
    Iterator<Subscriber> getSubscribers(String subject, Object event) {
        ImmutableSet<Class<?>> eventTypes = flattenHierarchy(event.getClass());

        List<Iterator<Subscriber>> subscriberIterators =
                Lists.newArrayListWithCapacity(eventTypes.size());

        for (Class<?> eventType : eventTypes) {
            CopyOnWriteArraySet<Subscriber> eventSubscribers = subscribers.get(eventType);
            if (eventSubscribers != null) {
                Iterator<Subscriber> subs = eventSubscribers.stream()
                        .filter(subscriber -> {
                            if (Strings.isNullOrEmpty(subscriber.getSubject())) {
                                return false;
                            }

                            return subject.equals(subscriber.getSubject());
                        }).iterator();
                // eager no-copy snapshot
                subscriberIterators.add(subs);
            }
        }

        return Iterators.concat(subscriberIterators.iterator());
    }

    /**
     * Returns all subscribers for the given listener grouped by the type of event they subscribe to.
     */
    private Multimap<Class<?>, Subscriber> findAllSubscribers(Object listener) {
        Multimap<Class<?>, Subscriber> methodsInListener = HashMultimap.create();
        Class<?> clazz = listener.getClass();
        for (Method method : getAnnotatedMethods(clazz)) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?> eventType = parameterTypes[0];
            methodsInListener.put(eventType, Subscriber.create(bus, listener, method));
        }
        return methodsInListener;
    }

    private static final class MethodIdentifier {

        private final String name;
        private final List<Class<?>> parameterTypes;

        MethodIdentifier(Method method) {
            this.name = method.getName();
            this.parameterTypes = Arrays.asList(method.getParameterTypes());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, parameterTypes);
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (o instanceof MethodIdentifier) {
                MethodIdentifier ident = (MethodIdentifier) o;
                return name.equals(ident.name) && parameterTypes.equals(ident.parameterTypes);
            }
            return false;
        }
    }
}
