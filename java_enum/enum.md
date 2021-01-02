## 枚举类

- 类的对象只有有限个，确定的。
   
   -> 星期：Monday(星期一),.....,Sunday(星期天)
   
   -> 性别：Man(男人),Woman(女人)
   
   -> 季节：Spring(),...,Winter()
   
   -> 订单状态：Nonpayment(未付款),Paid(已付款),Delivered(已发货),Return(退货),Checked(以确定),Fulfilled(以配货)
   
   
- 当需要定义一组常量时，强烈建议使用枚举类

- 枚举类的实现： 
  
  -> [JDK1.5之前需要自己定义枚举类](https://github.com/LiuBo-keep/java/tree/master/java_enum/src/com/denum/demo/demo01)
 
  -> [JDK1.5新增的enum关键字用于定义枚举类](https://github.com/LiuBo-keep/java/tree/master/java_enum/src/com/denum/demo/demo02)
  
- 若枚举类只有一个对象，则可以作为一种单例模式的实现方式

- 枚举类的属性：

 -> 枚举类对象的属性不应允许被改动，所以应该使用private final 修饰
 
 -> 枚举类的使用private final修饰的属性的应该在构造器中为其赋值
 
 -> 若枚举类显示的定义了带参数的构造器，则在列出枚举值时也必须对应的传入参数
 
 
## 枚举类的默认父类Enum主要API

| 方法 | 描述 |
|--|--|
| [valueOf](https://github.com/LiuBo-keep/java/tree/master/java_enum/src/com/denum/demo/demo03)|传递枚举类型的Class对象和枚举常量名称给静态方法valueOf，会得到与参数匹配的枚举常量|
|--|--|--|
|[toString](https://github.com/LiuBo-keep/java/tree/master/java_enum/src/com/denum/demo/demo04)|得到当前枚举类常量的名称，你可以通过重写这个方法来使得得到结果更易读|
|--|--|--|
|[equals](https://github.com/LiuBo-keep/java/tree/master/java_enum/src/com/denum/demo/demo05)|在枚举类型中可以直接使用"=="来比较两个枚举类常量是否相等。Enum提供的这个equals()方法，也是直接使用"=="实现的。他的存在是为了在Set，List和Map中使用。注意，equals()是不可变的|
|--|--|--|
|[hashCode](https://github.com/LiuBo-keep/java/tree/master/java_enum/src/com/denum/demo/demo06)|Enum实现了hashCode()来和equals()保持一致，他也是不可变的|
|--|--| 
|[getDeclaringClass](https://github.com/LiuBo-keep/java/tree/master/java_enum/src/com/denum/demo/demo07)|得到枚举类常量所属枚举类型的Class对象，可以用它来判断两个枚举类常量是否属于同一个枚举类型|
|--|--|
|[name](https://github.com/LiuBo-keep/java/tree/master/java_enum/src/com/denum/demo/demo08)|得到当前枚举类常量的名称，建议优先使用toString()|
|--|--|
|[ordinal](https://github.com/LiuBo-keep/java/tree/master/java_enum/src/com/denum/demo/demo09)|得到当前枚举类常量的次序|
|--|--|
|[compareTo](https://github.com/LiuBo-keep/java/tree/master/java_enum/src/com/denum/demo/demo10)|枚举类实现了Comparable接口，这样可以比较两个枚举常量的大小|
|--|--|
|[clone](https://github.com/LiuBo-keep/java/tree/master/java_enum/src/com/denum/demo/demo11)|枚举类型不能被Clone。为了防止子类实现克隆方法，Enum实现了一个仅抛出CloneNotSupportedException异常的不变Clone|
   