package test;

import org.junit.Test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @ClassName JdbcTest1
 * @Description 测试得到数据库连接
 * @Author 17126
 * @Date 2020/4/27 21:05
 */
public class JdbcTest1 {
    /*
     Driver 是一个连接操作数据库的总的接口，不
     同数据库厂商实现该接口具体实现自己厂商操作数据库的方法
    */

    //方式1
    @Test
    public void fun1() throws SQLException {
        //获取Driver实现对象
        Driver driver=new com.mysql.jdbc.Driver();

        //连接数据库的路径
        String url="jdbc:mysql://localhost:3306/wuliu";
        //数据库的用户名和密码
        Properties info=new Properties();
        info.setProperty("user","root");
        info.setProperty("password","liu");

        //获取连接
        Connection connect = driver.connect(url,info);

        System.out.println(connect);
    }

    //方式2：使用反射使程序具有更好的可移植性
    @Test
    public void fun2() throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        //获取Driver实现对象
        Class c= Class.forName("com.mysql.jdbc.Driver");
        Driver driver = (Driver) c.newInstance();

        //连接数据库的路径
        String url="jdbc:mysql://localhost:3306/wuliu";
        //数据库的用户名和密码
        Properties info=new Properties();
        info.setProperty("user","root");
        info.setProperty("password","liu");

        //获取连接
        Connection connect = driver.connect(url,info);

        System.out.println(connect);
    }

    //方式3：使用DriverManager
    @Test
    public void fun3() throws Exception{
        //获取Driver实现对象
        Class c= Class.forName("com.mysql.jdbc.Driver");
        Driver driver = (Driver) c.newInstance();
        //注册驱动
        DriverManager.registerDriver(driver);

        //连接数据库的路径
        String url="jdbc:mysql://localhost:3306/wuliu";
        //用户名
        String user="root";
        //密码
        String password="liu";

        //获取连接
        Connection connection = DriverManager.getConnection(url, user, password);

        System.out.println(connection);
    }

    //方式4：使用DriverManager
    @Test
    public void fun4() throws Exception{
        //加载驱动
        Class.forName("com.mysql.jdbc.Driver");

        //连接数据库的路径
        String url="jdbc:mysql://localhost:3306/wuliu";
        //用户名
        String user="root";
        //密码
        String password="liu";

        //获取连接
        Connection connection = DriverManager.getConnection(url, user, password);

        System.out.println(connection);
    }
}
