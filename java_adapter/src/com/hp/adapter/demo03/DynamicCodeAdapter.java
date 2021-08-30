package com.hp.adapter.demo03;

/**
 * @author LiuBo
 * @date 2021/8/30
 * @Description 描述
 */
public class DynamicCodeAdapter extends DynamicCodeUtil implements DynamicCode {
    @Override
    public String generateDynamicCode() {
        return createDynamicCode();
    }
}
