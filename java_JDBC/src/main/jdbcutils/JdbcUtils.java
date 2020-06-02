package main.jdbcutils;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @ClassName JdbcUtils
 * @Description TODO
 * @Author 17126
 * @Date 2020/6/2 9:30
 */
public class JdbcUtils<T> {
    private Connection connection=null;
    private Statement statement=null;

    public JdbcUtils(){
        Properties properties = new Properties();
        try{
            properties.load(new FileInputStream(new File("main/jdbcutils/myres.properties")));
            Class.forName(properties.getProperty("driver"));
            connection=DriverManager.getConnection(properties.getProperty("name"),
                    properties.getProperty("password"),
                    properties.getProperty("url"));
            statement=connection.createStatement();
        }catch (Exception E){
            E.printStackTrace();
        }
    }

    public T get(String sql){
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
