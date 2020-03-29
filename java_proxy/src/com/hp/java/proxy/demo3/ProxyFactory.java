package com.hp.java.proxy.demo3;

//创建代理工厂

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 工厂设置三样东西：
 * 1.目标对象
 * 2.前置增强
 * 3.后置增强
 * 调用createProxy方法得到代理对象
 * 执行代理方法时：
 * 1.执行前置增强
 * 2.目标对象方法
 * 3.后置增强
 */
public class ProxyFactory {
    private Object targetObject;//目标对象
    private BeforeAdvice beforeAdvice;//前置增强
    private AfterAdvice afterAdvice;//后置增强

    /**
     * 创建代理对象
     * @return
     */
    public Object createProxy(){
        //三大参数
        ClassLoader classLoader=this.getClass().getClassLoader();//类加载器
        Class [] classes=targetObject.getClass().getInterfaces();//目标对象实现的所有接口
        InvocationHandler h=(proxy,method,args)->{
            if (afterAdvice!=null){
                beforeAdvice.before();
            }
            Object result=method.invoke(targetObject,args);//调用目标对象的目标方法
            if (beforeAdvice!=null){
                beforeAdvice.before();
            }
            //返回目标对象方法的返回值
            return result;
        };

        Object proxy=Proxy.newProxyInstance(classLoader,classes,h);
        return proxy;
    }

    public Object getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(Object targetObject) {
        this.targetObject = targetObject;
    }

    public BeforeAdvice getBeforeAdvice() {
        return beforeAdvice;
    }

    public void setBeforeAdvice(BeforeAdvice beforeAdvice) {
        this.beforeAdvice = beforeAdvice;
    }

    public AfterAdvice getAfterAdvice() {
        return afterAdvice;
    }

    public void setAfterAdvice(AfterAdvice afterAdvice) {
        this.afterAdvice = afterAdvice;
    }
}
