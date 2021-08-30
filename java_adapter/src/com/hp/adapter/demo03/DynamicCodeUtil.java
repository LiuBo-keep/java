package com.hp.adapter.demo03;

import java.util.UUID;

/**
 * @author LiuBo
 * @date 2021/8/30
 * @Description 描述
 */
public class DynamicCodeUtil {

    public static String createDynamicCode() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}
