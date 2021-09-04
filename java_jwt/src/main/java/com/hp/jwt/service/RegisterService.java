package com.hp.jwt.service;

import com.hp.jwt.bean.RegisterRequest;
import com.hp.jwt.common.UserMap;
import com.hp.jwt.common.UserTypeEnum;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LiuBo
 * @date 2021/9/4
 * @Description 描述
 */

@Service
public class RegisterService extends UserMap{


    public Object register(RegisterRequest request) {

        String type = request.getType();
        if (request.getType().equals(UserTypeEnum.USER.name())) {
            this.userRegister(request.getUserName(), request.getPassword());
            return null;
        } else if (request.getType().equals(UserTypeEnum.THIRD_PARTY.name())) {
            return thirdParty(request.getCompanyName());
        } else {
            return null;
        }
    }


    private void userRegister(String userName, String password) {
        User user = new User();
        user.setUserName(userName);
        user.setPassword(password);
        this.put(userName, user);
    }

    private Map<String, String> thirdParty(String companyName) {
        ThirdParty thirdParty = new ThirdParty();
        thirdParty.setCompanyName(companyName);
        thirdParty.setClientId(UUID.randomUUID().toString().replace("_", ""));
        thirdParty.setClientSecret(Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes()));

        this.put(thirdParty.getClientId(), thirdParty);

        Map<String, String> map = new HashMap<>();
        map.put("clientId", thirdParty.getClientId());
        map.put("clientSecret", thirdParty.getClientSecret());
        return map;
    }


    @Data
    public class User {
        private String userName;
        private String password;
        private String userSecurityKey;
    }

    @Data
    public class ThirdParty {
        private String companyName;
        private String clientId;
        private String clientSecret;
        private String ThirdPartySecurityKey;
    }
}
