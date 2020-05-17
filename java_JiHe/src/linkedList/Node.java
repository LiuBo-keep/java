package linkedList;

/**
 * @ClassName Node
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/17 8:13
 */
public class Node {

    //最核心的变量，这个变量用来保存当前结点的数据
    public Object obj;

    public void setObj(Object obj){
        this.obj=obj;
    }

    //关键2：保存的是前一个结点
    public Node prev;

    //关键3：保存的是后一个结点
    public Node next;

    //带参的构造方法，更便于做结点
    public Node(Object obj, Node prev, Node next) {
        this.obj = obj;
        this.prev = prev;
        this.next = next;
    }

    //无参构造方法，便于做空结点
    public Node() {
    }
}
