package com.sceurity.springmvc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

/**
 * @ClassName ApplicationConfig
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/10 20:44
 */

@Configuration
@ComponentScan(basePackages = "com.sceurity.springmvc"
//排除扫描Controller注解所在类
,excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,value = Controller.class)})
public class ApplicationConfig {
    //在此配置处理Controller的其他的bean，比如：数据库连接池，事务管理器等
}
