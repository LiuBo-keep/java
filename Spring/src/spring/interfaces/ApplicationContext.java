package spring.interfaces;

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
}
