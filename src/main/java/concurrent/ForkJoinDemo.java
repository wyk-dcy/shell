package concurrent;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @author wuyongkang
 * @date 2023年03月10日 19:46
 */
public class ForkJoinDemo extends RecursiveTask<Long> {
    private static final int THRESHOLD = 10000;
    private final Long start;
    private final Long end;

    public ForkJoinDemo(Long start, Long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        long sum = 0;
        boolean canCompute = (end - start) < THRESHOLD;
        if (canCompute) {
            for (long i = start; i < end; i++) {
                sum += i;
            }
        } else {
            long step = (start + end) / 100;
            ArrayList<ForkJoinDemo> arrayList = new ArrayList<>();
            long pos = start;
            for (int i = 0; i < 100; i++) {
                long lastOne = pos + step;
                if (lastOne > end) {
                    lastOne = end;
                }
                ForkJoinDemo forkJoinDemo = new ForkJoinDemo(pos, lastOne);
                pos += step + 1;
                arrayList.add(forkJoinDemo);
                forkJoinDemo.fork();
            }
            for (ForkJoinDemo forkJoinDemo : arrayList) {
                sum += forkJoinDemo.join();
            }
        }
        return sum;
    }

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinDemo forkJoinDemo = new ForkJoinDemo(0L, 200000L);
        ForkJoinTask<Long> result = forkJoinPool.submit(forkJoinDemo);
        try {
            long res = result.get();
            System.out.println("sum=" + res);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}