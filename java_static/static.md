### Java中static关键字

static是java语言中的关键字，表示“静态的”，它可以用来**修饰变量、方法、代码块等，修饰的变量叫做静态变量，修饰的方法叫做静态方法，修饰的代码块叫做静态代码块**。在java语言
中凡是用static修饰的都是类相关的，不需要创建对象，直接通过“类名”即可访问，即使使用“引用”去访问，在运行的时候也和堆内存当中的对象无关。


有时你希望定义一个类成员，使它的使用完全独立于该类的任何对象。通常情况下，类成员必须通过它的类的对象访问，但是可以创建这样一个成员，它能够被它自己使用，而不必引用特定的实例。在成
员的声明前面加上关键字static（静态的）就能创建这样的成员。如果一个成员被声明为static，它就能够在它的类的任何对象创建之前被访问，而不必引用任何对象。你可以将方法和变量都声明为
static。static 成员的最常见的例子是main（）。因为在程序开始执行时必须调用main() ，所以它被声明为static。


声明为static的变量称为静态变量或类变量。可以直接通过类名引用静态变量，也可以通过实例名来引用静态变量，但最好采用前者，因为后者容易混淆静态变量和一般变量。静态变量是跟类相关联的，
类的所有实例共同拥有一个静态变量。


声明为static的方法称为静态方法或类方法。静态方法可以直接调用静态方法，访问静态变量，但是不能直接访问实例变量和实例方法。静态方法中不能使用this关键字，因为静态方法不属于任何一个
实例。


### [Java中static静态变量](https://github.com/LiuBo-keep/java/tree/master/java_static/src/study/statics/variable)
java中的变量包括：局部变量和成员变量，在方法体中声明的变量为局部变量，有效范围很小，只能在方法体中访问，方法结束之后局部变量内存就释放了，在内存方面局部变量存储在栈当中。在类体
中定义的变量为成员变量，而成员变量又包括实例变量和静态变量，当成员变量声明时使用了static关键字，那么这种变量称为静态变量，没有使用static关键字称为实例变量，实例变量是对象级别的，
每个对象的实例变量值可能不同，所以实例变量必须先创建对象，通过“引用”去访问，而静态变量访问时不需要创建对象，直接通过“类名”访问。实例变量存储在堆内存当中，静态变量存储在方法
区当中。**实例变量在构造方法执行过程中初始化，静态变量在类加载时初始化**。那么变量在什么情况下会声明为静态变量呢？[请看以下代码，定义一个“男人”类](https://github.com/LiuBo-keep/java/tree/master/java_static/src/study/statics/variable/demo01)：

运行结果如下图所示:
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201221105938212.png)

我们来看一下以上程序的内存结构图：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201221111438232.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)


“男人类”创建的所有“男人对象”，每一个“男人对象”的身份证号都不一样，该属性应该每个对象持有一份，所以应该定义为实例变量，而每一个“男人对象”的性别都是“男”，不会随着对象的
改变而变化，性别值永远都是“男”，这种情况下，性别这个变量还需要定义为实例变量吗，有必要让每一个“男人对象”持有一份吗，这样岂不是浪费了大量的堆内存空间，所以这个时候建议将“性
别=男”属性定义为类级别的属性，声明为静态变量，上升为“整个族”的数据，这样的变量不需要创建对象直接使用“类名”即可访问。[请看代码](https://github.com/LiuBo-keep/java/tree/master/java_static/src/study/statics/variable/demo02)：

运行结果如下图所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201221120238795.png)


我们来看一下以上程序的内存结构图：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201221120619216.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)


通过以上内容的学习我们得知，当一个类的所有对象的某个“属性值”不会随着对象的改变而变化的时候，建议将该属性定义为静态属性（或者说把这个变量定义为静态变量），静态变量在类加载的时
候初始化，存储在方法区当中，不需要创建对象，直接通过“类名”来访问。如果静态变量使用“引用”来访问，可以吗，如果可以的话，这个访问和具体的对象有关系吗？[来看以下代码](https://github.com/LiuBo-keep/java/tree/master/java_static/src/study/statics/variable/demo03)：

运行结果如下图所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201221152707200.png)


通过以上代码以及运行结果可以看出，静态变量也可以使用“引用”去访问，但实际上在执行过程中，“引用”所指向的对象并没有参与，如果是空引用访问实例变量，程序一定会发生空指针异常，但
是以上的程序编译通过了，并且运行的时候也没有出现任何异常，这说明虽然表面看起来是采用“引用”去访问，但实际上在运行的时候还是直接通过“类”去访问的。静态方法是这样吗？[请看以下代码](https://github.com/LiuBo-keep/java/tree/master/java_static/src/study/statics/variable/demo04)：

运行结果如下图所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201221153032172.png)


通过以上代码测试得知，静态变量和静态方法比较正式的方式是直接采用“类名”访问，但实际上使用“引用”也可以访问，并且空引用访问静态变量和静态方法并不会出现空指针异常。实际上，在开发
中并不建议使用“引用”去访问静态相关的成员，因为这样会让程序员困惑，因为采用“引用”方式访问的时候，程序员会认为你访问的是实例相关的成员。

总之，所有实例相关的，包括实例变量和实例方法，必须先创建对象，然后通过“引用”的方式去访问，如果空引用访问实例相关的成员，必然会出现空指针异常。所有静态相关的，包括静态变量和静态
方法，直接使用“类名”去访问。虽然静态相关的成员也能使用“引用”去访问，但这种方式并不被主张。


### [Java static静态代码块](https://github.com/LiuBo-keep/java/tree/master/java_static/src/study/statics/codeBlock)


### [Java static静态方法](https://github.com/LiuBo-keep/java/tree/master/java_static/src/study/statics/method)
