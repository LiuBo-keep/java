package spring.interfaces;

import com.sun.istack.internal.Nullable;

/**
 * @ClassName SingletonBeanRegistry
 * @Description TODO
 * @Author 17126
 * @Date 2021/6/27 12:27
 */
public interface SingletonBeanRegistry {
    void registerSingleton(String var1, Object var2);

    @Nullable
    Object getSingleton(String var1);

    boolean containsSingleton(String var1);

    String[] getSingletonNames();

    int getSingletonCount();

    Object getSingletonMutex();
}
