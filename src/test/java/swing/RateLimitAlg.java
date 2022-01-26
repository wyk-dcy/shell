package swing;

import junit.framework.TestCase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuyongkang
 * @date 2022年01月07日 18:10
 */
public class RateLimitAlg extends TestCase {
    private AtomicInteger currentCount = new AtomicInteger(0);

    private void add(){
        int updatedCount = currentCount.incrementAndGet();
        System.out.println(updatedCount);
    }

    public void test(){
        RateLimitAlg rateLimitAlg = new RateLimitAlg();
        for (int i = 0; i < 5; i++) {
            rateLimitAlg.add();
        }

    }

}