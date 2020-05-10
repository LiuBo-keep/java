##  基于session的认证方式

#### 1.认证流程
    基于Session认证的流程：用户认证成功后，在服务端生成用户相关的数据保存在session(当前会话)，而发送给客户端的session_id存放在cookie中，这样客户端请求时带上session_id就可以验证服务器端是否存在session数据，以此完成用户的合法校验。当用户退出系统或session过期销毁时，客户端的session_id也就无效了。执行流程如下图：


![在这里插入图片描述](https://img-blog.csdnimg.cn/20200510201544947.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)
 

     基于session的认证机制由servlet规范定制，Servlet容器已实现，用户通过HttpSession的操作方法即可实现，如下HttpSeesion相关的操作API：
     
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200510202051559.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)