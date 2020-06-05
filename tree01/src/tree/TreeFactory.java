package tree;

import java.util.Arrays;

/**
 * @ClassName TreeFactory
 * @Description TODO
 * @Author 17126
 * @Date 2020/6/4 8:38
 */
public class TreeFactory {
    //树的容量
    private static Integer capacty;

    //树结点数量
    private static int nodeCount;

    //存放Node类型的数组
    private static NodeTree[] elements;

    private TreeFactory() {
        capacty=10;
        elements = new NodeTree[capacty];
    }

    //自定义数组的长度
    public static void setSize(int nu){
        capacty=nu;
        elements  = new NodeTree[nu];
    }

    //获取当前数组长度
    public static Integer getLength(){
        if (capacty==null){
            return -1;
        }
        capacty=elements.length;
        return capacty;
    }

    //判断是否为空树
    public static boolean exists(){
        return capacty==null?true:false;
    }

    //设置树的根结点
    public static NodeTree setTreeRoot(int n){
        elements[0]=new NodeTree(n,-1);
        nodeCount++;
        return new NodeTree(n,-1);
    }

    //添加
    public static boolean add(Integer number,int j){
        //判断结点长度是否大于数组长度
       if (nodeCount>capacty){
           grow(elements,elements.length);
       }
        NodeTree nodeTree = new NodeTree(number, j);
        elements[nodeCount]=nodeTree;

        return true;
    }

    //扩容
    private static void grow(NodeTree[] elements, int length) {
        int newLength = length<<1;
        System.out.println(newLength);
        NodeTree [] tem = new NodeTree[newLength];
        for (int i = 0 ;i<elements.length;i++){
            tem[i]=elements[i];
        }
        elements =tem;
    }

}
