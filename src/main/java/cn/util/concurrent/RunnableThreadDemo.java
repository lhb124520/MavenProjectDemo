package cn.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *   1.Callable规定的方法是call()，而Runnable规定的方法是run().
 *   2.Callable的任务执行后可返回值，而Runnable的任务是不能返回值的。
 *   3.call() 方法可抛出异常，而run() 方法是不能抛出异常的。
 *   4.运行Callable任务可拿到一个Future对象， Future表示异步计算的结果。
 *   5.它提供了检查计算是否完成的方法，以等待计算的完成，并检索计算的结果。
 *   6.通过Future对象可了解任务执行情况，可取消任务的执行，还可获取任务执行的结果。
 *   7.Callable是类似于Runnable的接口，实现Callable接口的类和实现Runnable的类都是可被其它线程执行的任务。
 */
public class RunnableThreadDemo {
    private static int POOL_NUM = 30; // 线程池数量

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < POOL_NUM; i++) {
            RunnableThread thread = new RunnableThread();
            //线程停顿
            Thread.sleep(1000);
            executorService.execute(thread);
        }
        // 关闭线程池
        executorService.shutdown();
    }
}

class RunnableThread implements Runnable {
    @Override
    public void run() {
        System.out.println("通过线程池方式创建的线程Runnable方式：" + Thread.currentThread().getName() + " ");
    }
}