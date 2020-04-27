package com.hp.java.demo;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName MapDemo
 * @Description TODO
 * @Author 17126
 * @Date 2020/4/27 20:18
 */
public class MapDemo {

    public static void main(String[] args) {


        for (int i = 1; i <100 ; i++) {
            System.out.println(i);
        }

        Map<Object, Object> map = new HashMap<>();

        map.put("1","aa");
        map.put("2","aaa");
        map.put("3","aas");
        map.put("4","aad");
        map.put("5","aaf");

        System.out.println(map);
    }
}
