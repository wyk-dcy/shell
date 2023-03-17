package concurrent;

import java.util.concurrent.*;

public class ThreadPoolUtils {

    /**
     * New cpu intensive thread pool
     *
     * @param name the thread prefix name
     * @return a {@link Executor} instance
     */
    public static Executor newCpuIntensiveThreadExecutor(String name) {
        int coreSize = Runtime.getRuntime().availableProcessors();
        int maxPoolSize = coreSize * 2;
        return new ThreadPoolExecutor(coreSize, maxPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new NamedThreadFactory(name));
    }


}