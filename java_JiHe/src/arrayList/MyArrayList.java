package arrayList;

/**
 * @ClassName MyArrayList
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/17 7:51
 */
public class MyArrayList {

    //
    private Object [] elementData;

    //记录数组实际长度
    private int size;

    //通过构造方法，把一些初始化的事情办了
    //最开始当集合被创建时，就给他里面的数组定一个默认的长度

    public MyArrayList() {
        elementData = new Object[10];
    }

    public void add(Object obj){

        //关键点：数组的长度本身不可变，所以当我们把数组长度定为10之后
        //如果超出了，怎么办
        if (size>=elementData.length){
            //当数组容量不够的时候，来一个新的数组，并且长度为原来数组的1.5倍
            Object[] tem = new Object[elementData.length*2];
            System.arraycopy(elementData,0,tem,0,size);
            elementData=tem;
        }

        elementData[size++]=obj;
    }
}
