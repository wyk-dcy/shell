package concurrent;

import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Executor;

/**
 * @author wuyongkang
 * @date 2023年03月16日 19:30
 */
public class MoreExecutor {
    public static void main(String[] args) {
        Executor executor = MoreExecutors.directExecutor();
        executor.execute(() -> System.out.println(Thread.currentThread().getName()));
    }
}