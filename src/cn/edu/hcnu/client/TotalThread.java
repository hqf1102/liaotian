package cn.edu.hcnu.client;

import javax.swing.*;
import java.sql.*;

public class TotalThread extends Thread{
    JFrame f;
    String username;
    public TotalThread(JFrame f,String username) {
        this.f = f;
        this.username=username;
    }
/**
 * 该线程的功能是进行统计登录在线的人数
 * 并进行对应标题提示登录人数的刷新
 * */
    public  void run(){
        String url="jdbc:oracle:thin:@localhost:1521:orcl";
        String name="root";
        String passwords="1234";
        Connection con= null;
        ResultSet rs=null;
        PreparedStatement ps=null;
        try{
                String sql="select count(*) from users where status='online'";
                con = DriverManager.getConnection(url,name,passwords);
                ps=con.prepareStatement(sql);
                rs=ps.executeQuery();
                if(rs.next()){
                    f.setTitle("聊天室" + " - " + username + "     当前在线人数:" + rs.getInt(1));
                }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
