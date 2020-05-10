package com.sceurity.springmvc.dao;

import com.sceurity.springmvc.model.UserDto;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName AuthenticationDaoImpl
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/10 21:38
 */

@Repository
public class AuthenticationDaoImpl implements AuthenticationDao {
    private Map<String,UserDto> userMap=new HashMap<>();

    {
        userMap.put("zhangsan",new UserDto("1010","zhansan","123","张三","133433"));
        userMap.put("lisi",new UserDto("1011","lisi","456","李四","133445733"));
    }

    @Override
    public UserDto getUserDao(String username) {
        return userMap.get(username);
    }


}
