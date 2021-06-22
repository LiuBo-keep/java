package spring.interfaces;

import spring.MessageSourceResolvable;
import spring.exception.NoSuchMessageException;

import java.util.Locale;

/**
 * @Description 用于支持信息的国际化和包含参数的信息的替换
 * @Author 17126
 * @Date 2021/6/22 14:07
 */
public interface MessageSource {

    /**
     * 解析code对应的信息进行返回，如果对应的code不能被解析则返回默认信息defaultMessage。
     *
     * @param code 需要进行解析的code，对应资源文件中的一个属性名
     * @param args 需要用来替换code对应的信息中包含参数的内容，如：{0},{1,date},{2,time}
     * @param defaultMessage                                  当对应code对应的信息不存在时需要返回的默认值
     * @param locale                                          对应的Locale
     * @return
     */
    String getMessage(String code, Object[] args, String defaultMessage, Locale locale);

    /**
     * 解析code对应的信息进行返回，如果对应的code不能被解析则抛出异常NoSuchMessageException
     *
     * @param code   需要进行解析的code，对应资源文件中的一个属性名
     * @param args   需要用来替换code对应的信息中包含参数的内容，如：{0},{1,date},{2,time}
     * @param locale 对应的Locale
     * @throws NoSuchMessageException 如果对应的code不能被解析则抛出该异常
     */
    String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException;

    /**
     * 通过传递的MessageSourceResolvable对应来解析对应的信息
     *
     * @param resolvable
     * @param locale     对应的Locale
     * @return
     * @throws NoSuchMessageException 如不能解析则抛出该异常
     */
    String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException;
}
