package spring.interfaces;

import com.sun.istack.internal.Nullable;
import spring.interfaces.autowirecapablebeanfactory.AutowireCapableBeanFactory;
import spring.interfaces.hierarchicealbeanfactory.HierarchicealBeanFactory;
import spring.interfaces.listablebeanfactory.ListableBeanFactory;

/**
 * @ClassName ApplicationContext
 * @Description TODO
 * @Author 17126
 * @Date 2021/6/22 14:11
 */
public interface ApplicationContext extends MessageSource,
        ResourceLoader,
        AutowireCapableBeanFactory,
        ListableBeanFactory,
        HierarchicealBeanFactory,
        ApplicationEventPublisher{

    @Nullable
    String getId();

    String getApplicationName();

    String getDisplayName();

    long getStartupDate();

    @Nullable
    ApplicationContext getParent();

    AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;
}
