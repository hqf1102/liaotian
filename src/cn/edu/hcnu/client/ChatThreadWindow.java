package cn.edu.hcnu.client;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.*;
import java.sql.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
/**
 * 聊天线程
 */
public class ChatThreadWindow {
    private String name;
     JComboBox cb;
    JFrame f;
     JTextArea ta;
    private JTextField tf;
     int total;// 在线人数统计
      DatagramSocket ds;
     String username;
    public ChatThreadWindow(String username, DatagramSocket ds) {
       this.username=username;
        this.ds=ds;
        /*
         * 设置聊天室窗口界面
         */
        f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(600, 400);
     f.setTitle("聊天室" + " - " + username + "     当前在线人数:" + ++total);
        f.setLocation(300, 200);
        ta = new JTextArea();
        JScrollPane sp = new JScrollPane(ta);
        ta.setEditable(false);
        tf = new JTextField();
        cb = new JComboBox();
        cb.addItem("All");
        JButton jb = new JButton("私聊窗口");
        JPanel pl = new JPanel(new BorderLayout());
        pl.add(cb);
        pl.add(jb, BorderLayout.WEST);
        JPanel p = new JPanel(new BorderLayout());
        p.add(pl, BorderLayout.WEST);
        p.add(tf);
        f.getContentPane().add(p, BorderLayout.SOUTH);
        f.getContentPane().add(sp);
        f.setVisible(true);
        GetMessageThread getMessageThread=new GetMessageThread(this);
        getMessageThread.start();

        showXXXIntoChatRppm();

    }
    public void showXXXIntoChatRppm(){//广播
        String url="jdbc:oracle:thin:@localhost:1521:orcl";
        String name="root";
        String passwords="1234";

        Connection con= null;
        ResultSet rs=null;
        PreparedStatement ps=null;
        try {
            String sql="select username,ip,port from users where status='online'";
            con = DriverManager.getConnection(url,name,passwords);
            ps=con.prepareStatement(sql);
            rs=ps.executeQuery();
            while(rs.next()){
                String usernames=rs.getString("username");

                if(!usernames.equals(username)){
                    cb.addItem(usernames);
                    TotalThread totalThread=new TotalThread(f,username);
                    totalThread.start();
                   String ip=rs.getString("ip");
                    int post=rs.getInt("port");
                    byte [] ipB=new byte[4];
                    String ips[]=ip.split("\\.");
                    for (int i=0;i<ips.length;i++){
                        ipB[i]=(byte) Integer.parseInt(ips[i]);
                    }
                    String message=username+"进入聊天室";
                    byte [] m =message.getBytes();
                    DatagramPacket datagramPacket=new DatagramPacket(m,m.length);
                    datagramPacket.setAddress(InetAddress.getByAddress(ipB));
                    datagramPacket.setPort(post);
                    DatagramSocket datagramSocket=new DatagramSocket();
                    datagramSocket.send(datagramPacket);
                }



            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}