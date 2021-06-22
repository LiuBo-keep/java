package spring.interfaces.autowirecapablebeanfactory;

import spring.DependencyDescriptor;
import spring.NamedBeanHolder;
import spring.exception.BeansException;
import spring.interfaces.beanfactory.BeanFactory;
import sun.plugin.com.TypeConverter;

import java.util.Set;

/**
 * @Description 容器外的Bean使用依赖注入
 * @Author 17126
 * @Date 2021/6/22 14:08
 *
 *  AutowireCapableBeanFactory是在BeanFactory的基础上实现对已存在实例的管理。
 *  可以使用这个接口集成其他框架，捆绑并填充并不由Spring管理生命周期并已存在的实例。
 *
 *  需要注意的是，ApplicationContext接口没有实现AutowireCapableBeanFactory接口，
 *  因为应用代码很少用到此功能，如果需要的话，可以调用ApplicationContext的getAutowireCapableBeanFactory方法，来获取此接口的实例。
 *
 */
public interface AutowireCapableBeanFactory extends BeanFactory {

    /**
     * 常量，用于标识外部自动装配功能是否可用。但是此标识不影响正常的（基于注解的等）自动装配功能的使用
     */
    int AUTOWIRE_NO = 0;

    /**
     * 标识按名装配的常量
     */
    int AUTOWIRE_BY_NAME = 1;

    /**
     * 标识按类型自动装配的常量
     */
    int AUTOWIRE_BY_TYPE = 2;

    /**
     * 标识按照贪婪策略匹配出的最符合的构造方法来自动装配的常量
     */
    int AUTOWIRE_CONSTRUCTOR = 3;

    /**
     * 标识自动识别一种装配策略来实现自动装配的常量
     */
    @Deprecated
    int AUTOWIRE_AUTODETECT = 4;

    /**
     * 创建一个给定Class的实例。
     * 执行此Bean所有的关于Bean生命周期的接口方法如BeanPostProcessor
     * 此方法用于创建一个新实例，它会处理各种带有注解的域和方法，并且会调用所有Bean初始化时所需要调用的回调函数
     * 此方法并不意味着by-name或者by-type方式的自动装配，如果需要使用这写功能，可以使用其重载方法
     */
    <T> T createBean(Class<T> beanClass) throws BeansException;

    /**
     * Populate the given bean instance through applying after-instantiation callbacks
     * 通过调用给定Bean的after-instantiation及post-processing接口，对bean进行配置。
     * 此方法主要是用于处理Bean中带有注解的域和方法。
     * 此方法并不意味着by-name或者by-type方式的自动装配，如果需要使用这写功能，可以使用其重载方法autowireBeanProperties
     */
    void autowireBean(Object existingBean) throws BeansException;

    /**
     * Configure the given raw bean: autowiring bean properties, applying
     * 配置参数中指定的bean，包括自动装配其域，对其应用如setBeanName功能的回调函数。
     * 并且会调用其所有注册的post processor.
     * 此方法提供的功能是initializeBean方法的超集，会应用所有注册在bean definenition中的操作。
     * 不过需要BeanFactory 中有参数中指定名字的BeanDefinition。
     */
    Object configureBean(Object existingBean, String beanName) throws BeansException;


    /**
     * 创建一个指定class的实例，通过参数可以指定其自动装配模式（by-name or by-type）.
     * 会执行所有注册在此class上用以初始化bean的方法，如BeanPostProcessors等
     */
    Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

    /**
     * 通过指定的自动装配策略来初始化一个Bean。
     * 此方法不会调用Bean上注册的诸如BeanPostProcessors的回调方法
     */
    Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

    /**
     * 通过指定的自动装配方式来对给定的Bean进行自动装配。
     * 不过会调用指定Bean注册的BeanPostProcessors等回调函数来初始化Bean。
     * 如果指定装配方式为AUTOWIRE_NO的话，不会自动装配属性，但是依然会调用BeanPiostProcesser等回调方法。
     */
    void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck)
            throws BeansException;

    /**
     * 将参数中指定了那么的Bean，注入给定实例当中
     * 此方法不会自动注入Bean的属性，它仅仅会应用在显式定义的属性之上。如果需要自动注入Bean属性，使用
     * autowireBeanProperties方法。
     * 此方法需要BeanFactory中存在指定名字的Bean。除了InstantiationAwareBeanPostProcessor的回调方法外，
     * 此方法不会在Bean上应用其它的例如BeanPostProcessors
     * 等回调方法。不过可以调用其他诸如initializeBean等方法来达到目的。
     */
    void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException;

    /**
     * 初始化参数中指定的Bean，调用任何其注册的回调函数如setBeanName、setBeanFactory等。
     * 另外还会调用此Bean上的所有postProcessors 方法
     */
    Object initializeBean(Object existingBean, String beanName) throws BeansException;

    /**
     * 调用参数中指定Bean的postProcessBeforeInitialization方法
     */
    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws BeansException;

    /**
     * 调用参数中指定Bean的postProcessAfterInitialization方法
     */
    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeansException;

    /**
     * 销毁参数中指定的Bean，同时调用此Bean上的DisposableBean和DestructionAwareBeanPostProcessors方法
     * 在销毁途中，任何的异常情况都只应该被直接捕获和记录，而不应该向外抛出。
     */
    void destroyBean(Object existingBean);


    /**
     * 查找唯一符合指定类的实例，如果有，则返回实例的名字和实例本身
     * 和BeanFactory中的getBean(Class)方法类似，只不过多加了一个bean的名字
     */
    <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException;

    /**
     * 解析出在Factory中与指定Bean有指定依赖关系的Bean
     * 参数建下一个方法
     */
    Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName) throws BeansException;

    /**
     * 解析指定Bean在Factory中的依赖关系
     * @param descriptor 依赖描述 (field/method/constructor)
     * @param requestingBeanName 依赖描述所属的Bean
     * @param autowiredBeanNames 与指定Bean有依赖关系的Bean
     * @param typeConverter 用以转换数组和连表的转换器
     * @return the 解析结果，可能为null
     */
    Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName,
                             Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException;
}
