package eventbus;

/**
 * Handler for exceptions thrown by event subscribers.
 *
 * @author wuyongkang
 */
public interface SubscriberExceptionHandler {
    /**
     * Handles exceptions thrown by subscribers.
     *
     * @param exception exception
     * @param context   context
     */
    void handleException(Throwable exception, SubscriberExceptionContext context);
}
