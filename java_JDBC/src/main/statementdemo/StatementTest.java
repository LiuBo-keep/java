package main.statementdemo;

import org.junit.Test;

import java.sql.*;
import java.util.Scanner;

/**
 * @ClassName StatementTest
 * @Description TODO
 * @Author 17126
 * @Date 2020/4/28 0:04
 */
public class StatementTest {

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
        String sql="select username,password from user where user='"+name+"',password='"+password+"'";

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

            //获取操作数据库对象
             statement = connection.createStatement();
            //执行SQL
            rs = statement.executeQuery(sql);

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
