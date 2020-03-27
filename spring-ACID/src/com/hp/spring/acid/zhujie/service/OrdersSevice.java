package com.hp.spring.acid.zhujie.service;

import com.hp.spring.acid.zhujie.dao.OrdersDao;
import org.springframework.transaction.annotation.Transactional;

/*
 *@Transactional注解参数说明：
 * propagation：事务传播行为
 * timeout：事务超时时间，默认30s
 * isolation：事务隔离级别
 * readOnly：该属性用于设置当前事务是否为只读事务，设置为true表示只读，
 * false则表示可读写，默认值为false。
 * rollbackFor：该属性用于设置需要进行回滚的异常类数组，当方法中抛出指
 * 定异常数组中的异常时，则进行事务回滚
 * rollbackForClassName：该属性用于设置需要进行回滚的异常类名称数组，
 * 当方法中抛出指定异常名称数组中的异常时，则进行事务回滚
 * noRollbackFor：该属性用于设置不需要进行回滚的异常类数组，当方法
 * 中抛出指定异常数组中的异常时，不进行事务回滚
 * noRollbackForClassName：该属性用于设置不需要进行回滚的异常类名
 * 称数组，当方法中抛出指定异常名称数组中的异常时，不进行事务回滚
 */
@Transactional
public class OrdersSevice {

    private OrdersDao ordersDao;

    public OrdersSevice(OrdersDao ordersDao) {
        this.ordersDao = ordersDao;
    }

    //转账业务
    public void accountMoney(){
        //少钱
        ordersDao.lessMoney();
        //多钱
        ordersDao.addMoney();
    }
}
