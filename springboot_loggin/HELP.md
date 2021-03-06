## 日志框架
市场上存在非常多的日志框架，JUL(java.util.logging),JCL(Apache Commons Logging),Log4j,Log4j2,Logback,SLF4j,jboss-logging等。
SpringBoot在框架内容使用JCL，spring-boot-starter-logging采用了
slf4j+logback的形式，SpringBoot也能自动适配(jul,log4j2,logback)并简化配置。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200403152432600.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

## SLF4j使用
#### 1.在系统中使用SLF4j
以后开发的时候，日志记录方法的调用，不应该来直接调用日志的实现类，而是调用日志抽象层里面的方法；
给系统里面导入，slf4j的jar和logback的实现jar

```
import org.slf4j.logger;
import org.slf4j.LoggerFactory;

public class HelloWorld{
    public static void main(String[] args){
    Logger logger=LoggerFactory.getLogger(HelloWorld.class);
    logger.info("Hello world");
    }
}
```

图示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020040315444887.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

每一个日志的实现框架都有自己的配置文件。使用slf4j以后，配置文件还是做成日志实现框架自己本身的配置文 件； 

#### 2.遗留问题
a（slf4j+logback）: Spring（commons-logging）、Hibernate（jboss-logging）、MyBatis、xxxx
统一日志记录，即使是别的框架和我一起统一使用slf4j进行输出？
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200403155702391.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)


如何让系统中所有的日志都统一到slf4j；

 1、将系统中其他日志框架先排除出去；

2、用中间包来替换原有的日志框架；
 
 3、我们导入slf4j其他的实现
 

## SpringBoot日志关系
```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
```
```xml
     <!--SpringBoot使用他来做日志功能-->
                <dependency>
                   <groupId>org.springframework.boot</groupId>
                   <artifactId>spring-boot-starter-logging</artifactId>
                 </dependency>
```
底层依赖关系

  ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200403161042981.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)
  
总结：

  1）、SpringBoot底层也是使用slf4j+logback的方式进行日志记录
  
  2）、SpringBoot也把其他的日志都替换成了slf4j；
 
 3）、中间替换包？     
 ```java
 @SuppressWarnings("rawtypes") 
 public abstract class LogFactory{
    static String UNSUPPORTED_OPERATION_IN_JCL_OVER_SLF4J=
    "http://www.slf4j.org/codes.html#unsupported_operation_in_jcl_over_slf4j";
    static LogFactory logFactory = new SLF4JLogFactory();
 }
 ```   
 4)、如果我们要引入其他框架？一定要把这个框架的默认日志依赖移除掉？
      
      Spring框架用的是commons-logging；
      

```xml
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring.core</artifactId>
     <exclusions>
        <exclusion>
          <groupId>commons-logging</groupId>
          <artifactId>commons-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```
SpringBoot能自动适配所有的日志，而且底层使用slf4j+logback的方式记录日志，引入其他框架的时候，只需要 把这个框架依赖的日志框架排除掉即可；      
     
## 日志的输出
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200403164557947.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

## 日志的输出格式
```
 日志输出格式： 
  %d表示日期时间，
  %thread表示线程名，          
  %‐5level：级别从左显示5个字符宽度          
  %logger{50} 表示logger名字长50个字符，否则按照句点分割。           
  %msg：日志消息，          
  %n是换行符              
  ‐‐>     %d{yyyy‐MM‐dd HH:mm:ss.SSS} [%thread] %‐5level %logger{50} ‐ %msg%n

```
##SpringBoot修改日志的默认配置
```properties
#指定com包下的日志都是要debug级别的
#logging.level.com=debug

#在当前磁盘的根路径下创建spring文件夹和里面的log文件夹；使用spring.log作为默认日志文件
#logging.file.path=/spring/log

#不指定路径当前项目下生成springboot.log日志
#logging.file.name=springboot.log

#指定完整路径
#logging.file.name=M:/springboot.log

#在控制台输出的日志格式
#logging.pattern.console=%d{yyyy-MM-dd} [%thread] %-5level %logger{50} - %msg%n

#在指定文件中日志的输出格式
#logging.pattern.file=%d{yyyy-MM-dd} === [%thread] === %-5level === %logger{50} === %msg%n


```      

## 配置自己的日志文件
1> 给类路径下放上每个日志框架自己的配置文件即可；SpringBoot就不使用他默认配置的了
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200403172805539.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

logback.xml：直接就被日志框架识别了；

 logback-spring.xml：日志框架就不直接加载日志的配置项，由SpringBoot解析日志配置，可以使用SpringBoot 的高级Proﬁle功能

```xml
<springProfile name="staging">     
 <!--configuration to be enabled when the "staging" profile is active-->    
 可以指定某段配置只在某个环境下生效    
 </springProfile> 

```

如：
```xml
<appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">         
                <!--
                日志输出格式： 
                           %d表示日期时间，              
                           %thread表示线程名，              
                           %‐5level：级别从左显示5个字符宽度              
                           %logger{50} 表示logger名字长50个字符，否则按照句点分割。               
                           %msg：日志消息，              
                           %n是换行符       
                           -->       
           <layout class="ch.qos.logback.classic.PatternLayout">             
             <springProfile name="dev">                 
               <pattern>%d{yyyy‐MM‐dd HH:mm:ss.SSS} ‐‐‐‐> [%thread] ‐‐‐> %‐5level  %logger{50} ‐ %msg%n</pattern>             
             </springProfile>             
             <springProfile name="!dev">                 
               <pattern>%d{yyyy‐MM‐dd HH:mm:ss.SSS} ==== [%thread] ==== %‐5level  %logger{50} ‐ %msg%n</pattern>     
             </springProfile>         
           </layout>    
 </appender>
```

如果使用logback.xml作为日志配置文件，还要使用proﬁle功能，会有以下错误
no applicable action for [springProfile] 