package regex;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author wuyongkang
 * @date 2022年01月20日 9:44
 */
public class Regex extends AbstractQueuedSynchronizer {
    public static void main(String[] args) {
        boolean A = false;
        System.out.println(A |= false);
        System.out.println(A);
        System.out.println(A |= true);
        System.out.println(A);
        boolean B = true;
        System.out.println(B |= false);
        System.out.println(B);
        System.out.println(B |= true);
        System.out.println(B);
    }
}
