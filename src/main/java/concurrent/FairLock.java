package concurrent;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wuyongkang
 */
public class FairLock implements Runnable{
    // 如果是公平锁的话会交替获取锁,不是的话会出现一个线程重复的获取锁
    private static ReentrantLock fairLock = new ReentrantLock(false);
    @Override
    public void run() {
        while (true){
            try {
                fairLock.lock();
                System.out.println(Thread.currentThread().getName()+ "获取锁");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fairLock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        FairLock t1 = new FairLock();
        FairLock t2 = new FairLock();
        new Thread(t1).start();
        new Thread(t2).start();
    }
}