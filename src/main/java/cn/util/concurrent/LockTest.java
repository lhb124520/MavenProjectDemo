package cn.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 重入锁ReentrantLock实现公平锁和非公平锁
 * 公平锁线程执行会严格按照顺序执行，非公平锁多个线程竞争同一资源时
 *
 * @author lhb
 * @date 2020-11-11
 */
public class LockTest {
    /**
     * 因为ReentrantLock构造函数中可以直接传入一个boolean值fair，
     * 对公平性进行设置。当fair为true时，表示此锁是公平的，当fair为false时，表示此锁是非公平的锁；
     *
     * @param args
     */
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        ReentrantLock fairLock = new ReentrantLock(true);
//        ReentrantLock unFairLock = new ReentrantLock();
        for (int i = 0; i < 10; i++) {
            //公平锁
            threadPool.submit(new TestThread(fairLock, i, " fairLock"));
            //非公平锁
//            threadPool.submit(new TestThread(unFairLock, i, "unFairLock"));
        }
    }

    static class TestThread implements Runnable {
        Lock lock;
        int indext;
        String tag;

        public TestThread(Lock lock, int index, String tag) {
            this.lock = lock;
            this.indext = index;
            this.tag = tag;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getId() + " 线程 START  " + tag);
            meath();
        }

        private void meath() {
            lock.lock();
            try {
//                if ((indext & 0x1) == 1) {
//                    Thread.sleep(200);
//                } else {
//                    Thread.sleep(500);
//                }
                System.out.println(Thread.currentThread().getId() + " 线程 获得： Lock  ---》" + tag + "  Index:" + indext);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}
