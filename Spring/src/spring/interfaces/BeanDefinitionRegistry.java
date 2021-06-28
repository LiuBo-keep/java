package spring.interfaces;

import spring.exception.BeanDefinitionStoreException;
import spring.exception.NoSuchBeanDefinitionException;

/**
 * @ClassName BeanDefinitionRegistry
 * @Description Bean 定义注册表
 * @Author 17126
 * @Date 2021/6/27 11:20
 */
public interface BeanDefinitionRegistry extends AliasRegistry {

    /**
     * 关键 -> 往注册表中注册一个新的 BeanDefinition 实例
     */
    void registerBeanDefinition(String var1, BeanDefinition var2) throws BeanDefinitionStoreException;

    /**
     * 移除注册表中已注册的 BeanDefinition 实例
     */
    void removeBeanDefinition(String var1) throws NoSuchBeanDefinitionException;

    /**
     * 从注册中心取得指定的 BeanDefinition 实例
     */
    BeanDefinition getBeanDefinition(String var1) throws NoSuchBeanDefinitionException;

    /**
     * 判断 BeanDefinition 实例是否在注册表中（是否注册）
     */
    boolean containsBeanDefinition(String var1);

    /**
     * 取得注册表中所有 BeanDefinition 实例的 beanName（标识）
     */
    String[] getBeanDefinitionNames();

    /**
     * 返回注册表中 BeanDefinition 实例的数量
     */
    int getBeanDefinitionCount();

    /**
     * beanName（标识）是否被占用
     */
    boolean isBeanNameInUse(String var1);
}
