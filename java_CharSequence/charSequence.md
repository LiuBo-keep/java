### CharSequence

是一个描述字符串结构的接口，在这个接口中一般发现有三个常用的子类：

     String，StringBuffer，StringBuilder
     
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200519212334957.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)


### 现在只要有字符串就可以为CharSequence接口实例化
    public static void main(String[] args) {
            // 子类实例向父类接口转型
            CharSequence str ="hello"; 
        }   
        
### CharSequence本身是一个接口，在接口中定义的方法如下：
    1.获取指定索引字符
    public char charAt(int index);
    2.获取字符串的长度
    public int length();
    3.截取部分字符串
    public CharSequence subSequence(int start,int len);
    


### 注意（Sequence/siːkwəns/）
**看见CharSequence他就是一个字符串**    