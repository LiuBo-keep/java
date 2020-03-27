package com.hp.spring.acid.xml.dao;

import org.springframework.jdbc.core.JdbcTemplate;

public class OrdersDao {

    //注入jdbcTemplate
    private JdbcTemplate jdbcTemplate;

    public OrdersDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //少钱的方法
    public void lessMoney(){
        String sql="update spring_acid set salary=salary-? where username=?";
        jdbcTemplate.update(sql,1000,"小王");
    }
    //多钱的方法
    public void addMoney(){
        String sql="update spring_acid set salary=salary+? where username=?";
        jdbcTemplate.update(sql,1000,"小马");
    }
}
