package demo1;

/**
 * @ClassName Test1
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/23 0:25
 */

interface IMessage{
    //发送消息
    public void send();
}
/**
 *功能描述：实现消息的处理机制
 *@author
 *@date
  * @param null
 *@return
*/
class NetMessage implements IMessage{
    private String msg;

    public NetMessage(String msg) {
        this.msg = msg;
    }

    //获取资源链接
    public boolean open(){
        System.out.println("获取消息发送链接资源");
        return true;
    }

    //发送消息
    @Override
    public void send() {
        System.out.println("***发送消息***"+this.msg);
    }

    //关闭消息发送通道
    public void close(){
        System.out.println("关闭消息发送通道");
    }
}

public class Test1 {
    public static void main(String[] args) {
        //定义要发送的处理
        NetMessage message = new NetMessage("www.baidu.com");
        //判断是否获取连接
        if (message.open()){
            //发送消息
            message.send();
            //关闭资源通道
            message.close();
        }
    }
}
