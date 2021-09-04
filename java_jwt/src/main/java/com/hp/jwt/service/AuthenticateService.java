package com.hp.jwt.service;

import com.hp.jwt.bean.RegisterRequest;
import com.hp.jwt.common.Jwt;
import com.hp.jwt.common.UserMap;
import com.hp.jwt.common.UserTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author LiuBo
 * @date 2021/9/4
 * @Description 描述
 */

@Slf4j
@Service
public class AuthenticateService extends UserMap{

    @Autowired
    Jwt jwt;

    public String authenticate(RegisterRequest request) {
        String type = request.getType();
        if (request.getType().equals(UserTypeEnum.USER.name())) {
            this.userNamePasswordAuthenticate(request.getUserName(), request.getPassword());
            return null;
        } else if (request.getType().equals(UserTypeEnum.THIRD_PARTY.name())) {
            return thirdPartyAuthenticate(request.getCompanyName(), request.getClientId(), request.getClientSecret());
        } else {
            return null;
        }
    }

    public String userNamePasswordAuthenticate(String userName, String password) {
        RegisterService.User user = (RegisterService.User) this.get(userName);
        if (Objects.isNull(user)) {
            log.info("不存在此用户");
        }
        if (!password.equals(user.getPassword())) {
            log.info("密码错误");
        }

        return jwt.generateToken(userName, password, null, null);
    }

    public String thirdPartyAuthenticate(String companyName, String clientId, String clientSecret) {
        RegisterService.ThirdParty thirdParty = (RegisterService.ThirdParty) this.get(clientId);
        if (Objects.isNull(thirdParty)) {
            log.info("不存在此公司信息");
        }
        if (!clientId.equals(thirdParty.getClientId()) && !clientSecret.equals(thirdParty.getClientSecret())) {
            log.info("第三方认证失败");
        }

        return jwt.generateToken(null, null, clientId, clientSecret);
    }
}
