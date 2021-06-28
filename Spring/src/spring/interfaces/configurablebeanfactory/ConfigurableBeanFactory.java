package spring.interfaces.configurablebeanfactory;

import com.sun.istack.internal.Nullable;
import spring.exception.BeanDefinitionStoreException;
import spring.exception.NoSuchBeanDefinitionException;
import spring.interfaces.*;
import spring.interfaces.BeanFactory;
import spring.interfaces.HierarchicealBeanFactory;

import java.beans.PropertyEditor;
import java.security.AccessControlContext;

/**
 * @ClassName ConfigurableBeanFactory
 * @Description 父容器的设置 定义BeanFactory的配置.
 * @Author 17126
 * @Date 2021/6/22 14:10
 */
public interface ConfigurableBeanFactory extends HierarchicealBeanFactory,SingletonBeanRegistry {
    /**
     * 单例
     */
    String SCOPE_SINGLETON = "singleton";
    /**
     * 原型
     */
    String SCOPE_PROTOTYPE = "prototype";

    /**
     * 父容器设置.而且一旦设置了就不让修改
     */
    void setParentBeanFactory(BeanFactory var1) throws IllegalStateException;

    /**
     * 设置类加载器
     */
    void setBeanClassLoader(@Nullable ClassLoader var1);

    /**
     * 获取加载器，默认使用当前线程中的类加载器
     */
    @Nullable
    ClassLoader getBeanClassLoader();

    /**
     * 为了类型匹配,搞个临时类加载器.好在一般情况为null,使用上面定义的标准加载器
     */
    void setTempClassLoader(@Nullable ClassLoader var1);

    @Nullable
    ClassLoader getTempClassLoader();

    /**
     * 是否需要缓存bean metadata,比如bean difinition 和 解析好的classes.默认开启缓存
     */
    void setCacheBeanMetadata(boolean var1);

    /**
     * 是否开启缓存
     */
    boolean isCacheBeanMetadata();

    /**
     * 定义用于解析bean definition的表达式解析器
     */
    void setBeanExpressionResolver(@Nullable BeanExpressionResolver var1);

    @Nullable
    BeanExpressionResolver getBeanExpressionResolver();

    /**
     * 设置类型转化器
     */
    void setConversionService(@Nullable ConversionService var1);

    /**
     * 获取类型转化器
     */
    @Nullable
    ConversionService getConversionService();

    /**
     * 添加属性编辑器注册表
     */
    void addPropertyEditorRegistrar(PropertyEditorRegistrar var1);

    /**
     * 注册自定义编辑器
     */
    void registerCustomEditor(Class<?> var1, Class<? extends PropertyEditor> var2);

    /**
     * 将注册编辑复制到
     */
    void copyRegisteredEditorsTo(PropertyEditorRegistry var1);

    /**
     * 设置类型转换器
     */
    void setTypeConverter(TypeConverter var1);

    /**
     * 获取类型转换器
     */
    TypeConverter getTypeConverter();

    /**
     * 添加嵌入值解析器
     */
    void addEmbeddedValueResolver(StringValueResolver var1);

    /**
     * 具有嵌入式价值解析器
     */
    boolean hasEmbeddedValueResolver();

    /**
     * 解决内含价值
     */
    @Nullable
    String resolveEmbeddedValue(String var1);

    /**
     * BeanPostProcessor用于增强bean初始化功能
     * 设置一个Bean后处理器
     */
    void addBeanPostProcessor(BeanPostProcessor var1);

    /**
     * 返回Bean后处理器的数量
     */
    int getBeanPostProcessorCount();

    /**
     * 作用域定义
     * 注册范围
     */
    void registerScope(String var1, Scope var2);

    /**
     * 作用域定义
     * 返回注册的范围名
     */
    String[] getRegisteredScopeNames();

    /**
     * 作用域定义
     * 返回指定的范围
     */
    @Nullable
    Scope getRegisteredScope(String var1);

    /**
     * 设置应用程序启动
     */
    void setApplicationStartup(ApplicationStartup var1);

    /**
     * 作用域定义
     * 返回指定的范围
     */
    ApplicationStartup getApplicationStartup();

    /**
     * 访问权限控制
     * 返回本工厂的一个安全访问上下文
     */
    AccessControlContext getAccessControlContext();

    /**
     * 合并其他ConfigurableBeanFactory的配置,包括上面说到的BeanPostProcessor,作用域等
     * 从其他的工厂复制相关的所有配置
     */
    void copyConfigurationFrom(ConfigurableBeanFactory var1);

    /**
     * bean定义处理
     * 注册别名
     * 给指定的Bean注册别名
     */
    void registerAlias(String var1, String var2) throws BeanDefinitionStoreException;

    /**
     * bean定义处理
     * 根据指定的StringValueResolver移除所有的别名
     */
    void resolveAliases(StringValueResolver var1);

    /**
     * bean定义处理
     * 合并bean定义,包括父容器的
     * 返回指定Bean合并后的Bean定义
     */
    BeanDefinition getMergedBeanDefinition(String var1) throws NoSuchBeanDefinitionException;

    /**
     * bean定义处理
     * 是否是FactoryBean类型
     * 判断指定Bean是否为一个工厂Bean
     */
    boolean isFactoryBean(String var1) throws NoSuchBeanDefinitionException;

    /**
     * bean创建状态控制.在解决循环依赖时有使用
     * 设置一个Bean是否正在创建
     */
    void setCurrentlyInCreation(String var1, boolean var2);

    /**
     * bean创建状态控制.在解决循环依赖时有使用
     * 返回指定Bean是否已经成功创建
     */
    boolean isCurrentlyInCreation(String var1);

    /**
     * 处理bean依赖问题
     * 注册一个依赖于指定bean的Bean
     */
    void registerDependentBean(String var1, String var2);

    /**
     * 处理bean依赖问题
     * 返回依赖于指定Bean的所欲Bean名
     */
    String[] getDependentBeans(String var1);

    /**
     * 获取 Bean 的依赖关系
     * 返回指定Bean依赖的所有Bean名
     */
    String[] getDependenciesForBean(String var1);

    /**
     * bean生命周期管理-- 销毁bean
     * 销毁指定的Bean
     */
    void destroyBean(String var1, Object var2);

    /**
     * bean生命周期管理-- 销毁bean
     * 销毁指定的范围Bean
     */
    void destroyScopedBean(String var1);

    /**
     * bean生命周期管理-- 销毁bean
     * 销毁所有的单例类
     */
    void destroySingletons();
}
