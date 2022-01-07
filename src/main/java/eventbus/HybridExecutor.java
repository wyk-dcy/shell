package eventbus;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Executor;

/**
 * Hybrid Executor
 *
 * @author wuyongkang
 */
class HybridExecutor {
    private Executor syncExecutor;
    private Executor asyncExecutor;

    HybridExecutor(Executor asyncExecutor) {
        this.syncExecutor = MoreExecutors.directExecutor();
        this.asyncExecutor = Preconditions.checkNotNull(asyncExecutor);
    }

    public HybridExecutor(Executor syncExecutor, Executor asyncExecutor) {
        this.syncExecutor = Preconditions.checkNotNull(syncExecutor);
        this.asyncExecutor = Preconditions.checkNotNull(asyncExecutor);
    }

    void execute(Runnable command, boolean sync) {
        Executor executor = sync ? syncExecutor : asyncExecutor;
        executor.execute(command);
    }
}
