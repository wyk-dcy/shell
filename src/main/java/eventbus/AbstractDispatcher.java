package eventbus;

import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Handler for dispatching events to subscribers, providing different event ordering guarantees that
 * make sense for different situations.
 */
abstract class AbstractDispatcher {

    /**
     * Returns a dispatcher that queues events that are posted reentrantly on a thread that is already
     * dispatching an event, guaranteeing that all events posted on a single thread are dispatched to
     * all subscribers in the order they are posted.
     * <p>When all subscribers are dispatched to using a <i>direct</i> executor (which dispatches on
     * the same thread that posts the event), this yields a breadth-first dispatch order on each
     * thread. That is, all subscribers to a single event A will be called before any subscribers to
     * any events B and C that are posted to the event bus by the subscribers to A.
     */
    static AbstractDispatcher perThreadDispatchQueue() {
        return new PerThreadQueuedDispatcher();
    }

    /**
     * Returns a dispatcher that queues events that are posted in a single global queue. This behavior
     * matches the original behavior of AsyncEventBus exactly, but is otherwise not especially useful.
     * For async dispatch, an {@linkplain #immediate() immediate} dispatcher should generally be
     * preferable.
     */
    static AbstractDispatcher legacyAsync() {
        return new LegacyAsyncDispatcher();
    }

    /**
     * Returns a dispatcher that dispatches events to subscribers immediately as they're posted
     * without using an intermediate queue to change the dispatch order. This is effectively a
     * depth-first dispatch order, vs. breadth-first when using a queue.
     */
    static AbstractDispatcher immediate() {
        return ImmediateDispatcher.INSTANCE;
    }

    static AbstractDispatcher hybrid() {
        return new HybridDispatcher();
    }

    /**
     * Dispatches the given {@code event} to the given {@code subscribers}.
     */
    abstract void dispatch(Object event, Iterator<Subscriber> subscribers, boolean sync);

    /**
     * Implementation of a {@link #perThreadDispatchQueue()} dispatcher.
     */
    private static final class PerThreadQueuedDispatcher extends AbstractDispatcher {

        // This dispatcher matches the original dispatch behavior of EventBus.

        /**
         * Per-thread queue of events to dispatch.
         */
        private final ThreadLocal<Queue<Event>> queue =
                ThreadLocal.withInitial(Queues::newArrayDeque);

        /**
         * Per-thread dispatch state, used to avoid reentrant event dispatching.
         */
        private final ThreadLocal<Boolean> dispatching =
                ThreadLocal.withInitial(() -> false);

        @Override
        void dispatch(Object event, Iterator<Subscriber> subscribers, boolean sync) {
            Preconditions.checkNotNull(event);
            Preconditions.checkNotNull(subscribers);
            Queue<Event> queueForThread = queue.get();
            queueForThread.offer(new Event(event, subscribers));

            if (!dispatching.get()) {
                dispatching.set(true);
                try {
                    Event nextEvent;
                    while ((nextEvent = queueForThread.poll()) != null) {
                        while (nextEvent.subscribers.hasNext()) {
                            nextEvent.subscribers.next().dispatchEvent(nextEvent.event, sync);
                        }
                    }
                } finally {
                    dispatching.remove();
                    queue.remove();
                }
            }
        }

        private static final class Event {
            private final Object event;
            private final Iterator<Subscriber> subscribers;

            private Event(Object event, Iterator<Subscriber> subscribers) {
                this.event = event;
                this.subscribers = subscribers;
            }
        }
    }

    /**
     * Implementation of a {@link #legacyAsync()} dispatcher.
     */
    private static final class LegacyAsyncDispatcher extends AbstractDispatcher {

        // This dispatcher matches the original dispatch behavior of AsyncEventBus.
        //
        // We can't really make any guarantees about the overall dispatch order for this dispatcher in
        // a multithreaded environment for a couple reasons:
        //
        // 1. Subscribers to events posted on different threads can be interleaved with each other
        //    freely. (A event on one thread, B event on another could yield any of
        //    [a1, a2, a3, b1, b2], [a1, b2, a2, a3, b2], [a1, b2, b3, a2, a3], etc.)
        // 2. It's possible for subscribers to actually be dispatched to in a different order than they
        //    were added to the queue. It's easily possible for one thread to take the head of the
        //    queue, immediately followed by another thread taking the next element in the queue. That
        //    second thread can then dispatch to the subscriber it took before the first thread does.
        //
        // All this makes me really wonder if there's any value in queueing here at all. A dispatcher
        // that simply loops through the subscribers and dispatches the event to each would actually
        // probably provide a stronger order guarantee, though that order would obviously be different
        // in some cases.

        /**
         * Global event queue.
         */
        private final ConcurrentLinkedQueue<EventWithSubscriber> queue =
                Queues.newConcurrentLinkedQueue();

        @Override
        void dispatch(Object event, Iterator<Subscriber> subscribers, boolean sync) {
            Preconditions.checkNotNull(event);
            while (subscribers.hasNext()) {
                queue.add(new EventWithSubscriber(event, subscribers.next()));
            }

            EventWithSubscriber e;
            while ((e = queue.poll()) != null) {
                e.subscriber.dispatchEvent(e.event, sync);
            }
        }

        private static final class EventWithSubscriber {
            private final Object event;
            private final Subscriber subscriber;

            private EventWithSubscriber(Object event, Subscriber subscriber) {
                this.event = event;
                this.subscriber = subscriber;
            }
        }
    }

    /**
     * Implementation of {@link #immediate()}.
     */
    private static final class ImmediateDispatcher extends AbstractDispatcher {
        private static final ImmediateDispatcher INSTANCE = new ImmediateDispatcher();

        @Override
        void dispatch(Object event, Iterator<Subscriber> subscribers, boolean sync) {
            Preconditions.checkNotNull(event);
            while (subscribers.hasNext()) {
                subscribers.next().dispatchEvent(event, sync);
            }
        }
    }

    /**
     * Implementation of {@link #hybrid()}.
     */
    private static final class HybridDispatcher extends AbstractDispatcher {
        private AbstractDispatcher syncDispatcher;
        private AbstractDispatcher asyncDispatcher;

        HybridDispatcher() {
            this(AbstractDispatcher.perThreadDispatchQueue()
                    , AbstractDispatcher.legacyAsync());
        }

        HybridDispatcher(AbstractDispatcher syncDispatcher, AbstractDispatcher asyncDispatcher) {
            this.syncDispatcher = Preconditions.checkNotNull(syncDispatcher);
            this.asyncDispatcher = Preconditions.checkNotNull(syncDispatcher);
        }

        @Override
        void dispatch(Object event, Iterator<Subscriber> subscribers, boolean sync) {
            AbstractDispatcher dispatcher = sync ? syncDispatcher : asyncDispatcher;
            dispatcher.dispatch(event, subscribers, sync);
        }
    }
}
