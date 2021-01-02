## 枚举类

- 类的对象只有有限个，确定的。
   
   -> 星期：Monday(星期一),.....,Sunday(星期天)
   
   -> 性别：Man(男人),Woman(女人)
   
   -> 季节：Spring(),...,Winter()
   
   -> 订单状态：Nonpayment(未付款),Paid(已付款),Delivered(已发货),Return(退货),Checked(以确定),Fulfilled(以配货)
   
   
- 当需要定义一组常量时，强烈建议使用枚举类

- 枚举类的实现： 
  
  -> [JDK1.5之前需要自己定义枚举类]()
 
  -> [JDK1.5新增的enum关键字用于定义枚举类]()
  
- 若枚举类只有一个对象，则可以作为一种单例模式的实现方式

- 枚举类的属性：

 -> 枚举类对象的属性不应允许被改动，所以应该使用private final 修饰
 
 -> 枚举类的使用private final修饰的属性的应该在构造器中为其赋值
 
 -> 若枚举类显示的定义了带参数的构造器，则在列出枚举值时也必须对应的传入参数
 
 
## 枚举类的默认父类Enum主要API

| 方法 | 描述 |
|--|--|
| valueOf|  |
|--|--|
|toString||
|--|--|
|equals||
|--|--|
|hashCode||
|--|--| 
|getDeclaringClass||
|--|--|
|name||
|--|--|
|ordinal||
|--|--|
|compareTo||
|--|--|
|clone||
   