package com.hp.adapter.demo03;

/**
 * @author LiuBo
 * @date 2021/8/30
 * @Description 描述
 */
public class Test {

    public static void main(String[] args) {
        String dynamicCode = DynamicCodeUtil.createDynamicCode();
        System.out.println(dynamicCode);

        DynamicCode dynamicCodeAdapter = new DynamicCodeAdapter();
        String s = dynamicCodeAdapter.generateDynamicCode();
        System.out.println(s);
    }
}
