package concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wuyongkang
 */
public class ReenterLockCondition implements Runnable{
    public static ReentrantLock lockCondition =new ReentrantLock();
    public static Condition condition = lockCondition.newCondition();
    @Override
    public void run() {
        try {
            lockCondition.lock();
            condition.await();
            System.out.println("Thread isgoing on");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lockCondition.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReenterLockCondition t1 = new ReenterLockCondition();
        Thread thread = new Thread(t1);
        thread.start();
        Thread.sleep(2000);
        lockCondition.lock();
        condition.signal();
        lockCondition.unlock();
    }
}