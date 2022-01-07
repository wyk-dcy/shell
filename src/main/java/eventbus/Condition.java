package eventbus;

/**
 * Condition represents a condition for notify
 *
 * @author wuyongkang
 */
public interface Condition {
    /**
     * Always notification condition
     */
    Condition ALWAYS = new Always();

    /**
     * Returns a {@link Boolean} value represents whether or not to notify
     *
     * @return a {@link Boolean} value represents whether or not to notify
     */
    boolean shouldNotify();

    class Always implements Condition {
        @Override
        public boolean shouldNotify() {
            return true;
        }
    }
}
