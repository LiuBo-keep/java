package tree;

/**
 * @ClassName NodeTree
 * @Description Node结点
 * @Author 17126
 * @Date 2020/6/4 8:36
 */
public class NodeTree {
   //数据域
    private Integer data;
    //父节点指针
    private Integer number;

    public NodeTree(){

    }

    public NodeTree(Integer data, Integer number) {
        this.data = data;
        this.number = number;
    }

    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "NodeTree{" +
                "data=" + data +
                ", number=" + number +
                '}';
    }
}
