package spring.interfaces.hierarchicealbeanfactory;

import com.sun.istack.internal.Nullable;
import spring.interfaces.beanfactory.BeanFactory;

/**
 * @Description HierarchicalBeanFactory 提供父容器的访问功能
 * @Author 17126
 * @Date 2021/6/22 14:09
 *
 * 这个工厂接口非常简单，实现了Bean工厂的分层。这个工厂接口也是继承自BeanFacotory，也是一个二级接口，相对于父接口，
 *  它只扩展了一个重要的功能——工厂分层。
 */
public interface HierarchicealBeanFactory extends BeanFactory {

    /**
     * 返回本Bean工厂的父工厂
     */
    @Nullable
    BeanFactory getParentBeanFactory();

    /**
     * 本地工厂(容器)是否包含这个Bean
     */
    boolean containsLocalBean(String var1);
}
