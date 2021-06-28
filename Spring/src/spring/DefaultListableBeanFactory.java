package spring;

import spring.exception.BeanDefinitionStoreException;
import spring.exception.BeanEception;
import spring.exception.BeansException;
import spring.exception.NoSuchBeanDefinitionException;
import spring.interfaces.*;
import spring.interfaces.configurablebeanfactory.*;
import sun.plugin.com.TypeConverter;

import java.beans.PropertyEditor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.security.AccessControlContext;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author LiuBo
 * @date 2021/6/27
 * @Description 默认可列出 Bean 工厂
 */
public class DefaultListableBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry, Serializable {
    @Override
    public void registerBeanDefinition(String var1, BeanDefinition var2) throws BeanDefinitionStoreException {

    }

    @Override
    public void removeBeanDefinition(String var1) throws NoSuchBeanDefinitionException {

    }

    @Override
    public void ignoreDependencyType(Class<?> var1) {

    }

    @Override
    public void ignoreDependencyInterface(Class<?> var1) {

    }

    @Override
    public void registerResolvableDependency(Class<?> var1, Object var2) {

    }

    @Override
    public boolean isAutowireCandidate(String var1, DependencyDescriptor var2) throws NoSuchBeanDefinitionException {
        return false;
    }

    @Override
    public BeanDefinition getBeanDefinition(String var1) throws NoSuchBeanDefinitionException {
        return null;
    }

    @Override
    public Iterator<String> getBeanNamesIterator() {
        return null;
    }

    @Override
    public void clearMetadataCache() {

    }

    @Override
    public void freezeConfiguration() {

    }

    @Override
    public boolean isConfigurationFrozen() {
        return false;
    }

    @Override
    public void preInstantiateSingletons() throws BeansException {

    }

    @Override
    public boolean containsBeanDefinition(String var1) {
        return false;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return new String[0];
    }

    @Override
    public String[] getBeanNamesForType(ResolvableType type) {
        return new String[0];
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        return new String[0];
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return new String[0];
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return null;
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        return null;
    }

    @Override
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        return new String[0];
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
        return null;
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
        return null;
    }

    @Override
    public int getBeanDefinitionCount() {
        return 0;
    }

    @Override
    public boolean isBeanNameInUse(String var1) {
        return false;
    }

    @Override
    public void setParentBeanFactory(BeanFactory var1) throws IllegalStateException {

    }

    @Override
    public void setBeanClassLoader(ClassLoader var1) {

    }

    @Override
    public ClassLoader getBeanClassLoader() {
        return null;
    }

    @Override
    public void setTempClassLoader(ClassLoader var1) {

    }

    @Override
    public ClassLoader getTempClassLoader() {
        return null;
    }

    @Override
    public void setCacheBeanMetadata(boolean var1) {

    }

    @Override
    public boolean isCacheBeanMetadata() {
        return false;
    }

    @Override
    public void setBeanExpressionResolver(BeanExpressionResolver var1) {

    }

    @Override
    public BeanExpressionResolver getBeanExpressionResolver() {
        return null;
    }

    @Override
    public void setConversionService(ConversionService var1) {

    }

    @Override
    public ConversionService getConversionService() {
        return null;
    }

    @Override
    public void addPropertyEditorRegistrar(PropertyEditorRegistrar var1) {

    }

    @Override
    public void registerCustomEditor(Class<?> var1, Class<? extends PropertyEditor> var2) {

    }

    @Override
    public void copyRegisteredEditorsTo(PropertyEditorRegistry var1) {

    }

    @Override
    public void setTypeConverter(spring.interfaces.configurablebeanfactory.TypeConverter var1) {

    }

    @Override
    public spring.interfaces.configurablebeanfactory.TypeConverter getTypeConverter() {
        return null;
    }

    @Override
    public void addEmbeddedValueResolver(StringValueResolver var1) {

    }

    @Override
    public boolean hasEmbeddedValueResolver() {
        return false;
    }

    @Override
    public String resolveEmbeddedValue(String var1) {
        return null;
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor var1) {

    }

    @Override
    public int getBeanPostProcessorCount() {
        return 0;
    }

    @Override
    public void registerScope(String var1, Scope var2) {

    }

    @Override
    public String[] getRegisteredScopeNames() {
        return new String[0];
    }

    @Override
    public Scope getRegisteredScope(String var1) {
        return null;
    }

    @Override
    public void setApplicationStartup(ApplicationStartup var1) {

    }

    @Override
    public ApplicationStartup getApplicationStartup() {
        return null;
    }

    @Override
    public AccessControlContext getAccessControlContext() {
        return null;
    }

    @Override
    public void copyConfigurationFrom(ConfigurableBeanFactory var1) {

    }

    @Override
    public void registerAlias(String var1, String var2) {

    }

    @Override
    public void resolveAliases(StringValueResolver var1) {

    }

    @Override
    public BeanDefinition getMergedBeanDefinition(String var1) throws NoSuchBeanDefinitionException {
        return null;
    }

    @Override
    public boolean isFactoryBean(String var1) throws NoSuchBeanDefinitionException {
        return false;
    }

    @Override
    public void setCurrentlyInCreation(String var1, boolean var2) {

    }

    @Override
    public boolean isCurrentlyInCreation(String var1) {
        return false;
    }

    @Override
    public void registerDependentBean(String var1, String var2) {

    }

    @Override
    public String[] getDependentBeans(String var1) {
        return new String[0];
    }

    @Override
    public String[] getDependenciesForBean(String var1) {
        return new String[0];
    }

    @Override
    public void destroyBean(String var1, Object var2) {

    }

    @Override
    public void destroyScopedBean(String var1) {

    }

    @Override
    public void destroySingletons() {

    }

    @Override
    public void removeAlias(String var1) {

    }

    @Override
    public boolean isAlias(String var1) {
        return false;
    }

    @Override
    public Object getBean(String name) throws BeanEception {
        return null;
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeanEception {
        return null;
    }

    @Override
    public Object getBean(String name, Object... args) throws BeanEception {
        return null;
    }

    @Override
    public boolean containsBean(String name) {
        return false;
    }

    @Override
    public boolean isSingLeton(String name) throws NoSuchBeanDefinitionException {
        return false;
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return false;
    }

    @Override
    public boolean isTypeMatch(String name, Class targetType) throws NoSuchBeanDefinitionException {
        return false;
    }

    @Override
    public Class getType(String name) throws NoSuchBeanDefinitionException {
        return null;
    }

    @Override
    public String[] getAliases(String var1) {
        return new String[0];
    }

    @Override
    public <T> T createBean(Class<T> beanClass) throws BeansException {
        return null;
    }

    @Override
    public void autowireBean(Object existingBean) throws BeansException {

    }

    @Override
    public Object configureBean(Object existingBean, String beanName) throws BeansException {
        return null;
    }

    @Override
    public Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
        return null;
    }

    @Override
    public Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
        return null;
    }

    @Override
    public void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck) throws BeansException {

    }

    @Override
    public void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException {

    }

    @Override
    public Object initializeBean(Object existingBean, String beanName) throws BeansException {
        return null;
    }

    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        return null;
    }

    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        return null;
    }

    @Override
    public void destroyBean(Object existingBean) {

    }

    @Override
    public <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException {
        return null;
    }

    @Override
    public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName) throws BeansException {
        return null;
    }

    @Override
    public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName, Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException {
        return null;
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return null;
    }

    @Override
    public boolean containsLocalBean(String var1) {
        return false;
    }

    @Override
    public void registerSingleton(String var1, Object var2) {

    }

    @Override
    public Object getSingleton(String var1) {
        return null;
    }

    @Override
    public boolean containsSingleton(String var1) {
        return false;
    }

    @Override
    public String[] getSingletonNames() {
        return new String[0];
    }

    @Override
    public int getSingletonCount() {
        return 0;
    }

    @Override
    public Object getSingletonMutex() {
        return null;
    }
}
