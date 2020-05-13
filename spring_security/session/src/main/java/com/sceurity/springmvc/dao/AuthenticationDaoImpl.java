package com.sceurity.springmvc.dao;

import com.sceurity.springmvc.model.UserDto;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        Set<String> authorities1=new HashSet<>();
        authorities1.add("p1");//这个p1认为让他和/r/r1对应
        Set<String> authorities2=new HashSet<>();
        authorities2.add("p2");//这个p1认为让他和/r/r2对应
        userMap.put("zhangsan",new UserDto("1010","zhansan","123","张三","133433",authorities1));
        userMap.put("lisi",new UserDto("1011","lisi","456","李四","133445733",authorities2));
    }

    @Override
    public UserDto getUserDao(String username) {
        return userMap.get(username);
    }


}
