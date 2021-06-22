package com.example.demo.bean;

/**
 * @author LiuBo
 * @date 2021/6/22
 * @Description 用户实体
 */
public class User {

    private String name;

    private String phone;


    public User() {
    }

    public User(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
