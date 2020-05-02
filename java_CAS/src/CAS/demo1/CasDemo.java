package CAS.demo1;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName CasDemo
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/2 17:46b
 *
 * CAS:比较并交换(compareAndSet)
 */
public class CasDemo {

    public static void main(String[] args) {

        //定义变量初始值为5
        AtomicInteger integer=new AtomicInteger(5);

        System.out.println(integer.compareAndSet(6,6));

        System.out.println(integer.get());


        //Unsafe:是CAS的核心类，由于Java方法无法直接访问底层系统，需要通过本地方法(native)方法来访问，Unsafe相当于一个后门，基于
        //该类可以直接操作特定内存的数据，Unsafe类存在与sun.misc包中，其内部方法操作可以像C的指针一样直接操作内存，因为Java中CAS操作
        //的执行依赖与Unsafe类的方法
        //注意：Unsafe类中的所有方法都是native修饰的，也就是说Unsafe类中的方法都直接调用操作系统底层资源执行相应任务
        //
        //CAS的全称是Compare-And-Swap,它是一条CPU并发原语
        //他的功能是判断内存某个位置的值是否为预期值，如果是则更改为新的值，这个过程是原子的
        //
        //CAS并发原语现在Java语言中就是sun.misc.Unsafe类中的各个方法。调用Unsafe类中的CAS方法，JVM会帮我们实现出CAS汇编指令。这是一种
        //完全依赖硬件的功能，通过他实现了原子操作。再次强调，由于CAS是一种系统原语，原语属于操作系统用语范畴，是由若干条指令组成的，用于
        //某个功能的一个过程，并且原语的执行必须是连续的，在执行过程中不允许被中断，也就是说CAS是一条CPU的原子指令，不会造成所谓的数据不一致问题。
        integer.getAndIncrement();

        //方法分析
        //public final int getAndIncrement() {
        //return unsafe.getAndAddInt(this, valueOffset, 1);this:当前对象，valueOffset:内存地址，1:要加的数
        //}

        //public final int getAndAddInt(Object var1,long var2,int var4){
        //     int var5;
        //     do{
        //        var5 = this.getInVolatile(var1,var2);
        //       }while(!this.compareAndSwapInt(var1,var2,var5,var5+var4)){
        //         return var5;
        //        }
        // }

        //CAS缺点：
        //1.循环时间长开销大
        //2.只能保证一个共享变量的原子操作
        //3.引出ABA问题
    }
}
