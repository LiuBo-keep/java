package com.sceurity.springmvc.init;

import com.sceurity.springmvc.config.ApplicationConfig;
import com.sceurity.springmvc.config.WebConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * @ClassName SpringApplicationInitializer
 * @Description 类似于web.xml配置
 * @Author 17126
 * @Date 2020/5/10 21:01
 */
public class SpringApplicationInitializer extends
        AbstractAnnotationConfigDispatcherServletInitializer {
    //spring容器,相当与加载 applicationContext.xml
    @Override
    protected Class<?>[] getRootConfigClasses() {
        //指定rootContext的配置类
        return new Class<?>[]{ApplicationConfig.class};
    }

    //servletContext,相当于加载springmvc.xml
    @Override
    protected Class<?>[] getServletConfigClasses() {
        //指定servletContext配置类
        return new Class<?>[]{WebConfig.class};
    }

    //url-mapping
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
