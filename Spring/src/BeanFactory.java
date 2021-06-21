import exception.BeanEception;
import exception.NoSuchBeanDefinitionException;

/**
 * IOC容器的基本方法
 * BeanFactory提供的是最基本的IOC容器的功能
 */
public interface BeanFactory {

    /***/
    String FACTORY_BEAN_PREFIX = "&";

    Object getBean(String name) throws BeanEception;

    <T> T getBean(String name, Class<T> requiredType) throws BeanEception;

    Object getBean(String name, Object... args) throws BeanEception;

    boolean containsBean(String name);

    boolean isSingLeton(String name) throws NoSuchBeanDefinitionException;

    boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

    boolean isTypeMatch(String name, Class targetType) throws NoSuchBeanDefinitionException;

    Class getType(String name) throws NoSuchBeanDefinitionException;

    String[] getAliases(String name);
}
