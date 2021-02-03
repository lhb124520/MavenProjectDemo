package cn.util.concurrent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class ThreadPoolExecutorTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int corePoolSize = 2;
        int maximumPoolSize = 4;
        long keepAliveTime = 1L;
        TimeUnit unit = TimeUnit.SECONDS;
        ArrayBlockingQueue<Runnable> arrayBlockingQueue = new ArrayBlockingQueue<>(4);
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("线程-%d").build();
        ThreadPoolExecutor.DiscardOldestPolicy policy = new ThreadPoolExecutor.DiscardOldestPolicy();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, arrayBlockingQueue, threadFactory, policy);

        threadPoolExecutor.prestartAllCoreThreads(); // 预启动所有核心线程

        for (int i = 1; i <= 10; i++) {
            MyTask task = new MyTask();
            Future<?> future = threadPoolExecutor.submit(task);
            System.out.println(future.get());
        }

        //阻塞主线程
        threadPoolExecutor.shutdown();
    }

    static class MyTask implements Runnable {
        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName());
                //让任务执行慢点
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
