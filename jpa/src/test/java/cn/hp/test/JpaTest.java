package cn.hp.test;

import cn.hp.domain.Customer;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * @author LiuBo
 * @date 2021/7/1
 * @Description 描述
 */
public class JpaTest {

    /**
     * 测试Jpa的保存
     * 保存一个客户数据到数据库
     * <p>
     * Jpa的操作步骤：
     * 1.加载配置文件创建工厂(实体管理器类工厂)对象
     * 2.通过实体管理类工厂获取实体管理器
     * 3.获取事务对象，开启事务
     * 4.完成增删改查
     * 5.提交事务(回滚事务)
     * 6.资源释放
     */
    @Test
    public void testSave() {
        // 1.加载配置文件创建工厂(实体管理器类工厂)对象
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("myJpa");
        // 2.通过实体管理类工厂获取实体管理器
        EntityManager manager = factory.createEntityManager();
        // 3.获取事务对象，开启事务
        EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        // 4.完成增删改查:保存一个客户到数据库中
        Customer customer = new Customer();
        customer.setCustName("测试s");
        customer.setCustIndustry("测试哈哈");
        // 保存
        manager.persist(customer);
        // 提交事务(回滚事务)
        transaction.commit();
        // 6.资源释放
        manager.close();
        factory.close();
    }
}
