package demo1;

/**
 * @ClassName Test1
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/24 14:06
 */
public class Test1 {

    public static void main(String[] args) {
        Runtime runtime = Runtime.getRuntime();
        System.out.println("最大线程数="+runtime.availableProcessors());
        System.out.println("最大可用内存空间="+runtime.maxMemory()/1024/1024);
        System.out.println("可用内存空间="+runtime.totalMemory()/1024/1024);
        System.out.println("空闲的内存空间="+runtime.freeMemory()/1024/1024);
    }
}
