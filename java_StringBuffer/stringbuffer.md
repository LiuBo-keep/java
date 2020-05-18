### StringBuffer(1.0)
     public final class StringBuffer
     extends Object
     implements Serializable, CharSequence
     
    此类被final所修饰
    
 ### 简介
 线程安全，可变的字符序列。 字符串缓冲区就像一个String ，但可以修改。 在任何时间点，它包含一些特定的字符序列，但可以通过某些方法调用来更改序列的长度和内容。 
 
 字符串缓冲区可以安全地被多个线程使用。 这些方法在必要时进行同步，以便任何特定实例上的所有操作都按照与所涉及的各个线程所执行的方法调用顺序一致的顺序发生。 
 
 StringBuffer的主要**StringBuffer是append和insert方法**，它们被重载以便接受任何类型的数据。 每个都有效地将给定的数据转换为字符串，然后将该字符串的字符附加或
 插入到字符串缓冲区。 append方法总是在缓冲区的末尾添加这些字符; insert方法将insert添加到指定点。 
 
 例如，如果z是指当前内容为"start"的字符串缓冲区对象，那么方法调用z.append("le")将使字符串缓冲区包含"startle" ，而z.insert(4, "le")会将字符串缓冲区更改为包含"starlet" 。 
 
 
 从版本JDK 5开始，这个类别已经被一个等级类补充了，这个类被设计为使用一个线程StringBuilder 。 StringBuilder应该使用StringBuilder类，因为它支持所有相同的操作，但它更快，因为它不执行同步。 
 
### 常用的构造方法

     StringBuffer() 
     构造一个没有字符的字符串缓冲区，初始容量为16个字符
     
     StringBuffer(String str) 
     构造一个初始化为指定字符串内容的字符串缓冲区。
     
### 常用方法

    1.追加方法(方法重载，可以追加任意)
     append(boolean b) 
     将 boolean参数的字符串表示附加到序列中。
     
     append(char c) 
     将 char参数的字符串表示附加到此序列。
     
     append(char[] str) 
     将 char数组参数的字符串表示附加到此序列。
     
     append(char[] str, int offset, int len) 
     将 char数组参数的子阵列的字符串表示附加到此序列。
      
     append(double d) 
     将 double参数的字符串表示附加到此序列。 
     
     append(float f) 
     将 float参数的字符串表示附加到此序列。 
     
     append(int i) 
     将 int参数的字符串表示附加到此序列。
      
     append(long lng) 
     将 long参数的字符串表示附加到此序列。
     
     append(Object obj) 
     追加 Object参数的字符串表示。
      
     append(String str) 
     将指定的字符串附加到此字符序列。
      
     append(StringBuffer sb) 
     将指定 StringBuffer这个序列。
           
    2.删除
    delete(int start, int end) 
    删除此序列的子字符串中的字符。 
    
    deleteCharAt(int index) 
    删除 char在这个序列中的指定位置。
    
    3.插入(方法重载，可以插入多种类型)
    insert(int offset, boolean b) 
    在此序列中插入 boolean参数的字符串表示形式。
    
    insert(int offset, char c) 
    在此序列中插入 char参数的字符串表示形式。 
    
    insert(int offset, char[] str) 
    在此序列中插入 char数组参数的字符串表示形式。 
    
    insert(int offset, char[] str) 
    在此序列中插入 char数组参数的字符串表示形式。 
    
    insert(int offset, double d) 
    在此序列中插入 double参数的字符串表示形式。 
    
    insert(int offset, float f) 
    在此序列中插入 float参数的字符串表示形式。 
    
    insert(int offset, int i) 
    将第二个 int参数的字符串表示插入到此序列中。
    
    insert(int offset, long l) 
    在此序列中插入 long参数的字符串表示形式。 
    
    insert(int offset, Object obj) 
    将 Object参数的字符串表示插入到此字符序列中。 
    
    insert(int offset, String str) 
    将字符串插入到此字符序列中。 
    
    4.转换字符串
    toString() 
    返回表示此顺序中的数据的字符串。 