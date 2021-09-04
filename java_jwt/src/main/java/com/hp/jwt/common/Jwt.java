package com.hp.jwt.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.hp.jwt.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author LiuBo
 * @date 2021/9/4
 * @Description 描述
 */

@Component
public class Jwt extends UserMap {

    /**
     * token 过期时间: 10天
     */
    public static final int calendarField = Calendar.DATE;
    public static final int calendarInterval = 10;

    /**
     * @return java.lang.String
     * @Description TODO 生成令牌
     * @Param [userName, password, clintId, clientSecret]
     * @date 2021/9/4 12:39
     * @author liubo
     */
    public String generateToken(String userName, String password, String clintId, String clientSecret) {
        String sub = userName == null ? clintId : userName;
        String secret = generateSalt(sub);

        Calendar nowTime = Calendar.getInstance();
        nowTime.add(calendarField, calendarInterval);
        Date expiresDate = nowTime.getTime();

        // header Map
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        String token = JWT.create().withHeader(map)
                .withClaim("sub", sub)
                .withIssuedAt(new Date())
                .withExpiresAt(expiresDate)
                .sign(Algorithm.HMAC256(secret));


        return token;
    }

    public static Map<String, Claim> verifyToken(String token,String secret){
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            jwt = verifier.verify(token);
        } catch (Exception e) {
            // e.printStackTrace();
            // token 校验失败, 抛出Token验证非法异常
        }
        return jwt.getClaims();
    }

    private String generateSalt(String s) {

        String secret = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());

        if (s.length() == 36) {
            RegisterService.ThirdParty thirdParty = (RegisterService.ThirdParty) this.get(s);
            thirdParty.setThirdPartySecurityKey(secret);
        } else {
            RegisterService.User user = (RegisterService.User) this.get(s);
            user.setUserSecurityKey(secret);
        }
        return secret;
    }
}
