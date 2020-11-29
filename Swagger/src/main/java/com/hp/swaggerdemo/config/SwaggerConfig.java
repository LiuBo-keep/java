package com.hp.swaggerdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @ClassName SwaggerConfig
 * @Description TODO
 * @Author 17126
 * @Date 2020/11/29 12:58
 * <p>
 * 判断当前的开发环境，如果是生产环境则开启swagger，线上环境则关闭
 */

@Configuration
// 开启Swagger
@EnableSwagger2
public class SwaggerConfig {

    /**
     * 个人信息
     */
    private static final Contact DEFAULT_CONTACT = new Contact("kepp_liu", "https://github.com/LiuBo-keep", "1712624418@qq.com");


    @Bean
    public Docket docket1(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("仓库");
    }

    @Bean
    public Docket docket2(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("博客");
    }

    // 配置Swagger的Bean实例
    @Bean
    public Docket docket(Environment environment) {

        // 设置要显示的环境
        Profiles of = Profiles.of("dev", "test");
        // 通过environment.acceptsProfiles判断是否处在自己设定的环境当中
        boolean flag = environment.acceptsProfiles(of);

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                // enable是否开启Swagger，如果为false，则Swagger不能在浏览器中访问，反之可以
                .enable(flag)
                .select()
                // RequestHandlerSelectors 配置要扫描接口的方式
                .apis(RequestHandlerSelectors.basePackage("com.hp.swaggerdemo.controller")) // 指定扫描包
                .build();
    }

    @Bean
    public ApiInfo apiInfo() {
        return new ApiInfo(
                "学习Swagger",
                "JAVA之Swagger",
                "1.0",
                "http://39.102.72.175:8002/administrators/login",
                DEFAULT_CONTACT,
                "TreeHouse 1.0",
                "http://39.102.72.175:8002/administrators/login");
    }
}
