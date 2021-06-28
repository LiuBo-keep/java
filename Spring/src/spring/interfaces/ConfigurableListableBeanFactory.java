package spring.interfaces;

import com.sun.istack.internal.Nullable;
import spring.DependencyDescriptor;
import spring.exception.BeansException;
import spring.exception.NoSuchBeanDefinitionException;
import spring.interfaces.configurablebeanfactory.ConfigurableBeanFactory;

import java.util.Iterator;

/**
 * @ClassName ConfigurableListableBeanFactory
 * @Description 可配置的可列表 Bean 工厂
 * @Author 17126
 * @Date 2021/6/27 12:22
 */
public interface ConfigurableListableBeanFactory extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

    /**
     * 忽略依赖类型
     */
    void ignoreDependencyType(Class<?> var1);

    /**
     * 忽略依赖接口
     */
    void ignoreDependencyInterface(Class<?> var1);

    /**
     * 注册可解析依赖
     */
    void registerResolvableDependency(Class<?> var1, @Nullable Object var2);

    /**
     * 是 Autowire 候选者
     */
    boolean isAutowireCandidate(String var1, DependencyDescriptor var2) throws NoSuchBeanDefinitionException;

    /**
     * 获取 Bean 定义
     */
    BeanDefinition getBeanDefinition(String var1) throws NoSuchBeanDefinitionException;

    /**
     * 获取 Bean 名称迭代器
     */
    Iterator<String> getBeanNamesIterator();

    void clearMetadataCache();

    /**
     * 清除元数据缓存
     */
    void freezeConfiguration();

    /**
     * 配置冻结
     */
    boolean isConfigurationFrozen();

    /**
     * 预实例化单例
     */
    void preInstantiateSingletons() throws BeansException;
}
