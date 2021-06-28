package spring.interfaces;

import spring.exception.BeanEception;
import spring.exception.NoSuchBeanDefinitionException;

/**
 * IOC容器的基本方法
 * BeanFactory提供的是最基本的IOC容器的功能
 */
public interface BeanFactory {

    /**
     * 获取FactoryBean本身
     */
    String FACTORY_BEAN_PREFIX = "&";

    /**
     * 通过指定名称来获取对应的bean
     */
    Object getBean(String name) throws BeanEception;

    /**
     * 通过指定名称(name)同时指定所需类型(requiredType)获取对应的bean
     */
    <T> T getBean(String name, Class<T> requiredType) throws BeanEception;

    Object getBean(String name, Object... args) throws BeanEception;

    /**
     * 判断容器中是否含有指定名字的bean
     */
    boolean containsBean(String name);

    /**
     * 查询指定名字的bean是否是单例类型的bean，对于Singleton属性，可以在BeanDefinition中指定
     */
    boolean isSingLeton(String name) throws NoSuchBeanDefinitionException;

    /**
     * 查询指定名字的Bean是否是prototype类型的
     */
    boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

    /**
     * 查询指定名字的bean的Class类型是否是特定的Class类型，这个类型由用户指定
     */
    boolean isTypeMatch(String name, Class targetType) throws NoSuchBeanDefinitionException;

    /**
     * 查询指定名字的bean的Class类型
     */
    Class getType(String name) throws NoSuchBeanDefinitionException;

    /**
     * 查询指定名字的bean的所有别名，这些别名都是用户在BeanDefinition中定义的
     */
    String[] getAliases(String name);
}
