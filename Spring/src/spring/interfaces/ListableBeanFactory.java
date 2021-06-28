package spring.interfaces;

import spring.ResolvableType;
import spring.exception.BeansException;
import spring.exception.NoSuchBeanDefinitionException;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author LiuBo
 * @date 2021/6/22
 * @Description 提供容器内bean实例的枚举功能.这边不会考虑父容器内的实例.
 *
 * ListableBeanFactory接口是BeanFactory接口的一个扩展。实现了此接口的类一般都有预加载bean定义功能（从XML等配置文件中），
 * 因此都有能列举其包含的所有Bean，根据名字或其它单个查找Bean的特性。理论上讲，即使此类的实现类同时也实现了HierarchicalBeanFactory，
 * 此接口中方法返回值也不应该考虑其父BeanFactory中的Bean，但是你可以通过BeanFactoryUtils工具类来获取哪些在父BeanFactory中的Bean。
 * 此接口中的所有方法只会考虑本BeanFactory实例中的Bean定义，除了getBeanNamesOfType和getBeanOfType以外，其他方法都不会考虑其他方法
 * 诸如的bean实例,诸如通过ConfigurableBeanFactory的registerSingleton方法配置的单例。这主要也是因为getBean方法可以访问这些手动配置
 * 的Bean定义。但是，通常情况下，所有的Bean都是通过外部的配置文件配置的，因此不必担心以上这点区别。需要注意的是，除了getBeanDefinitionCount
 * 和containsBeanDefinition外，此接口中其他方法的实现可能不会太高效，因此，不建议经常调用。
 */
public interface ListableBeanFactory extends BeanFactory {

    /**
     * 查看是否包含指定名字的Bean
     * 不支持向上或向下查找
     * 不支持查找非配置文件定义的单例Bean
     */
    boolean containsBeanDefinition(String beanName);

    /**
     * 查看此BeanFactory中包含的Bean数量
     * 不支持向上或向下查找
     * 不支持查找非配置文件定义的单例Bean
     */
    int getBeanDefinitionCount();

    /**
     * 返回此BeanFactory中所包含的所有Bean定义的名称
     * 不支持向上或向下查找
     * 不支持查找非配置文件定义的单例Bean
     */
    String[] getBeanDefinitionNames();

    /**
     * 返回此BeanFactory中所有指定类型的Bean的名字。判断是否是指定类型的标准有两个：a Bean定义。
     * b FactoryBean的getObjectType方法。
     * 只考虑最顶层的Bean，对于嵌套的Bean，即使符合类型也不予考虑
     * 会考虑FactoryBean创建出的Bean
     * 不支持向上或向下查找
     * 不支持查找非配置文件定义的单例Bean
     * 此签名的getBeanNamesForType方法会返回所有Scope类型的Bean，在大多数的实现中，其返回结果和
     * 其重载方法getBeanNamesForType(type, true, true)返回结果一致。
     * 返回结果中，Bean名字的顺序应该和其定义时一样
     */
    String[] getBeanNamesForType(ResolvableType type);


    /**
     * 返回此BeanFactory中所有指定类型（或指定类型的子类型）的Bean的名字。判断是否是指定类型的标准有
     * 两个：a Bean定义，b FactoryBean的getObjectType方法。
     * 只考虑最顶层的Bean，对于嵌套的Bean，即使符合类型也不予考虑
     * 会考虑FactoryBean创建出的Bean
     * 不支持向上或向下查找
     * 不支持查找非配置文件定义的单例Bean
     * 此签名的getBeanNamesForType方法会返回所有Scope类型的Bean，在大多数的实现中，其返回结果和
     * 其重载方法getBeanNamesForType(type, true, true)返回结果一致。
     * 返回结果中，Bean名字的顺序应该和其定义时一样
     */
    String[] getBeanNamesForType(Class<?> type);


    /**
     * 返回此BeanFactory中所有指定类型（或指定类型的子类型）的Bean的名字。判断是否是指定类型的标准有
     * 两个：a Bean定义，b FactoryBean的getObjectType方法。
     * 只考虑最顶层的Bean，对于嵌套的Bean，即使符合类型也不予考虑
     * 会考虑FactoryBean创建出的Bean
     * 不支持向上或向下查找
     * 不支持查找非配置文件定义的单例Bean
     * 此签名的getBeanNamesForType方法会返回所有Scope类型的Bean，在大多数的实现中，其返回结果和
     * 其重载方法getBeanNamesForType(type, true, true)返回结果一致。
     * 返回结果中，Bean名字的顺序应该和其定义时一样
     * 如果Bean是通过FactoryBean创建的，那么只考虑设置了allowEagerInit标志位的Bean。如果
     * 没有设置allowEagerInit标志位，则只考虑FactoryBean的类型
     */
    String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit);


    /**
     * 返回此BeanFactory中所有指定类型（或指定类型的子类型）的Bean的名字。判断是否是指定类型的标准有
     * 两个：a Bean定义，b FactoryBean的getObjectType方法。
     * 只考虑最顶层的Bean，对于嵌套的Bean，即使符合类型也不予考虑
     * 会考虑FactoryBean创建出的Bean
     * 不支持向上或向下查找
     * 不支持查找非配置文件定义的单例Bean
     * 此签名的getBeanNamesForType方法会返回所有Scope类型的Bean，在大多数的实现中，其返回结果和
     * 其重载方法getBeanNamesForType(type, true, true)返回结果一致。
     * 返回结果中，Bean名字的顺序应该和其定义时一样
     */
    <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException;

    /**
     * 返回此BeanFactory中所有指定类型（或指定类型的子类型）的Bean的名字。判断是否是指定类型的标准有
     * 两个：a Bean定义，b FactoryBean的getObjectType方法。
     * 只考虑最顶层的Bean，对于嵌套的Bean，即使符合类型也不予考虑
     * 会考虑FactoryBean创建出的Bean
     * 不支持向上或向下查找
     * 不支持查找非配置文件定义的单例Bean
     * 此签名的getBeanNamesForType方法会返回所有Scope类型的Bean，在大多数的实现中，其返回结果和
     * 其重载方法getBeanNamesForType(type, true, true)返回结果一致。
     * 返回结果中，Bean名字的顺序应该和其定义时一样
     * 如果Bean是通过FactoryBean创建的，那么只考虑设置了allowEagerInit标志位的Bean。如果
     * 没有设置allowEagerInit标志位，则只考虑FactoryBean的类型
     */
    <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
            throws BeansException;

    /**
     * 找到所有带有指定注解类型的Bean
     */
    String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType);

    /**
     * 找到所有带有指定注解的Bean，返回一个以Bean的name为键，其对应的Bean实例为值的Map
     */
    Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException;

    /**
     * 在指定name对应的Bean上找指定的注解，如果没有找到的话，去指定Bean的父类或者父接口上查找
     */
    <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
            throws NoSuchBeanDefinitionException;
}
