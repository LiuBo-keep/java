package spring.interfaces;

import spring.utils.ResourceUtils;

/**
 * @Description 加载资源的策略接口
 * @Author 17126
 * @Date 2021/6/22 14:07
 *
 * 而Spring框架为了更方便的获取资源，尽量弱化程序员对各个Resource接口实现类的感知与分辨，降低学习与使用成本，
 * 定义了另一个接口，就是：ResourceLoader接口。
 *
 *  此接口有一个特别重要的方法：Resource getResource(String location)。返回的对象，就是Spring容器中Resource接口的实例
 * Spring内所有的ApplicationContext实例(包括Spring自启动容器或者用户手动创建的其他容器)，都实现了这个方法
 *
 * 因此程序员在使用Spring容器时，可以：
 *   - 不去过于计较Spring内底层Resource的实现方式
 *   - 也不需要自己创建Resource实现类
 *   - 而是直接使用applicationContext.getResource()语句，即可获取到applicationContext容器本身自有的Resource实例；
 *   - 进而用此Resource实例，去获取相关的资源数据。
 */
public interface ResourceLoader {

    /**
     * 用于从类路径上加载的伪URL前缀
     */
    String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;

    /**
     * 返回指定资源位置的资源句柄
     */
    Resource getResource(String location);

    /**
     * 暴露此ResourceLoader使用的ClassLoader
     */
    ClassLoader getClassLoader();
}
