package main.preparedstatementdemo;

import main.statementdemo.User;
import org.junit.Test;

import java.sql.*;
import java.util.Scanner;

/**
 * @ClassName PreparedStatementTest
 * @Description TODO
 * @Author 17126
 * @Date 2020/4/28 14:01
 */
public class PreparedStatementTest {

    //使用Statement的弊端，需要拼写SQL语句，并且存在SQL注入问题
    //如何避免SQL注入：只要用PreparedStatement(从Statement扩展而来)取代Statement
    @Test
    public void testLogin(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入用户名:");
        String name = scanner.next();
        System.out.println("请输入密码:");
        String password = scanner.next();

        //SQL注入:select username,password from user where user='1' or',password='=1 or '1'='1'"
        String sql="select username,password from user where user=? ,password=?";

        User user = get(sql, User.class);

        if (null!=user){
            System.out.println("成功");
        }else {
            System.out.println("失败");
        }
    }

    private <T> T get(String sql, Class<T> userClass) {
        T t=null;
        Connection connection=null;
        Statement statement=null;
        ResultSet rs=null;

        try{
            //加载驱动
            Class.forName("com.mysql.jdbc.Driver");

            //连接数据库的路径
            String url="jdbc:mysql://localhost:3306/wuliu";
            //用户名
            String user="root";
            //密码
            String password="liu";

            //获取连接
            connection = DriverManager.getConnection(url, user, password);

            //对SQL语句进行预编译
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            //给SQL语句进行赋值
            preparedStatement.setString(1,"");
            preparedStatement.setString(2,"");

            //执行SQL语句
            rs=preparedStatement.executeQuery();
            
            while (rs.next()){
                String usern = rs.getString("usern");
                String password1 = rs.getString("password");
                User user1 = new User();
                user1.setUsername(usern);
                user1.setPassword(password1);
                t= (T) user1;
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (null!=t){
            return t;
        }else {
            return null;
        }
    }
}
