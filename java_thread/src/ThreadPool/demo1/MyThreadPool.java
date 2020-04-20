package ThreadPool.demo1;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 程序启动一个新线程成本是比较高的，因为他涉及到要与操作系统进行交互。而使用线程池可以很好的提高性能，尤其是当程序中要创建大量生存期很短的线程时，更应该考虑使用线程池。
 * 线程池里的每一个线程代码结束后，并不会死亡，而是再次回到线程池中成为空闲状态，等待下一个对象来使用。
 * 在JDK5之前，我们必须手动实现自己的线程池，从JDK5开始，JAVA内置支持线程池。
 * JDK5新增了一个Executors工厂类来产生线程池，有如下几个方法：
 *      创建带有缓存的线程池：
 *        public static ExecutorService newCachedThreadPool();
 *        Executors.newCachedThreadPool();
 *      创建指定个数的线程池：
 *        public static ExecutorService newFixedThreadPool(int nThreads);
 *        Executors.newFixedThreadPool(int nThreads);
 *        参数：想造的线程个数
 *      创建只有一个线程的线程池：
 *        public static ExecutorService newSingleThreadExecutor();
 *        Executors.newSingleThreadExecutor();
 *        这些方法的返回值是ExecutorService对象，该对象表示一个线程池，可以执行Runnable对象或者Callable对象代表的线程。他提供的方法如下：
 *        Future<?>submit(Runnbale task);
 *        线程池对象.submit(Runnbale task);
 *        <T>Future<T>submit(Callable<T>task)
 *        线程池对象.submit(Runnbale task);
 *
 *     扩展：启动一次顺序关闭，执行以前提交的任务，但不接受新的任务
 *       public void shutdown();
 *       线程池对象.shutdown();
 */
public class MyThreadPool {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //创建线程池
        ExecutorService pool=Executors.newFixedThreadPool(5);

        //提交线程任务给线程池,使用Runnable实现线程任务
        pool.execute(()->{
            System.out.println("aaaaaa");
        });

        //提交线程任务给线程池,使用Callable实现线程任务
        Future future=pool.submit(()->{
           System.out.println("Callable");
           return "OK";
        });

        System.out.println(future.get());

        //关闭线程池
        pool.shutdown();
    }
}
