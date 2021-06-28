package spring.exception;

/**
 * @ClassName NoSuchBeanDefinitionException
 * @Description TODD
 * @Author liubo
 * @Date 2021/6/21 22:53
 * @Version 1.0
 **/
public class NoSuchBeanDefinitionException extends Exception{

    public NoSuchBeanDefinitionException(){

    }

    public NoSuchBeanDefinitionException(String msg){
        super(msg);
    }
}
