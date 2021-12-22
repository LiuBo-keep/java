package org.springframework.security.core.context;

import java.lang.reflect.Constructor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class SecurityContextHolder {
	// 认证后的信息存放策略
	// ThreadLocal
	// INHERITABLETHREADLOCAL
	// GLOBAL
    public static final String MODE_THREADLOCAL = "MODE_THREADLOCAL";
    public static final String MODE_INHERITABLETHREADLOCAL = "MODE_INHERITABLETHREADLOCAL";
    public static final String MODE_GLOBAL = "MODE_GLOBAL";
	
	// 系统参数
    public static final String SYSTEM_PROPERTY = "spring.security.strategy";
	// 获取策略名称
    private static String strategyName = System.getProperty("spring.security.strategy");
	// 策略接口
    private static SecurityContextHolderStrategy strategy;
	
	// 初始化次数
    private static int initializeCount = 0;

    public SecurityContextHolder() {
    }

    private static void initialize() {
		// 判断字符串不为空
        if (!StringUtils.hasText(strategyName)) {
			// 默认使用策略 ThreadLocal
            strategyName = "MODE_THREADLOCAL";
        }
		
		// 获取策略具体实现
        if (strategyName.equals("MODE_THREADLOCAL")) {
            strategy = new ThreadLocalSecurityContextHolderStrategy();
        } else if (strategyName.equals("MODE_INHERITABLETHREADLOCAL")) {
            strategy = new InheritableThreadLocalSecurityContextHolderStrategy();
        } else if (strategyName.equals("MODE_GLOBAL")) {
            strategy = new GlobalSecurityContextHolderStrategy();
        } else {
            try {
                Class<?> clazz = Class.forName(strategyName);
                Constructor<?> customStrategy = clazz.getConstructor();
                strategy = (SecurityContextHolderStrategy)customStrategy.newInstance();
            } catch (Exception var2) {
                ReflectionUtils.handleReflectionException(var2);
            }
        }

        ++initializeCount;
    }
	
	// 清除上下文
    public static void clearContext() {
        strategy.clearContext();
    }
	
	// 获取上下文
    public static SecurityContext getContext() {
        return strategy.getContext();
    }

    // 获取初始化上下文次数
    public static int getInitializeCount() {
        return initializeCount;
    }
	
	// 设置上下文
    public static void setContext(SecurityContext context) {
        strategy.setContext(context);
    }
	
	// 设置策略名称并初始化
    public static void setStrategyName(String strategyName) {
        SecurityContextHolder.strategyName = strategyName;
        initialize();
    }

    // 获取当前策略
    public static SecurityContextHolderStrategy getContextHolderStrategy() {
        return strategy;
    }

    public static SecurityContext createEmptyContext() {
        return strategy.createEmptyContext();
    }

    public String toString() {
        return "SecurityContextHolder[strategy='" + strategyName + "'; initializeCount=" + initializeCount + "]";
    }

    // 类加载时初始化策略
    static {
        initialize();
    }
}
