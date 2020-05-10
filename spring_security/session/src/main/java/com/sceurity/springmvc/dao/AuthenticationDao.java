package com.sceurity.springmvc.dao;

import com.sceurity.springmvc.model.UserDto;

/**
 * @ClassName AuthenticationDao
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/10 21:37
 */
public interface AuthenticationDao {

    UserDto getUserDao(String username);
}
