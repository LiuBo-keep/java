package linkedList;

/**
 * @ClassName MyLinkedList
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/17 8:23
 */
public class MyLinkedList {

    //临时记录最开始的结点
    private Node first;
    //临时记录最后的一个结点
    private Node last;
    //记录长度
    private int size;

    public void add(Object obj){
        Node node = new Node();
        //判断最开始的结点是否为空
        if (null == first){
            //当第一个结点都不存在，集合是空的时候
            node.prev=null;
            node.next=null;
            node.setObj(obj);

            first = node;
            last = node;
        } else {
          //当第一个结点存在之后，直接往后关联就行了
          //当前结点的上
            node.prev=last;
          //当前结点的后
            node.next =null;
          //当前结点存放的元素
            node.setObj(obj);
           //将是上一个结点的记录下一个结点的next记录当前结点
            last.next=node;
           //将last重新记录当前元素
            last=node;
        }
    }
}
