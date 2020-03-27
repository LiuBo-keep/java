package com.hp.spring.acid.xml.service;

import com.hp.spring.acid.xml.dao.OrdersDao;

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
