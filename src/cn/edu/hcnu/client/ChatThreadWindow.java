package cn.edu.hcnu.client;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.util.Map;
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
     JTextField tf;
    static int total;// 在线人数统计
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
        tf.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_ENTER){//回车键的判断
                    String str=(String)cb.getSelectedItem();
                    if(str.equals("All")){//群发
                        showinfoQun();
                    }else{
                        showsifa(str);
                        System.out.println("条件2");
                    }
                    tf.setText("");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
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
        GetMessageThread getMessageThread=new GetMessageThread(this);/**接收消息的线程*/
        getMessageThread.start();/**启用线程*/
        showXXXIntoChatRppm();/**进行广播登录人员*/
        showXXXInChatRoom();//提示正在聊天
    }
    public void showsifa(String na){//单播
        String url="jdbc:oracle:thin:@localhost:1521:orcl";
        String name="root";
        String passwords="1234";
        Connection con= null;
        ResultSet rs=null;
        PreparedStatement ps=null;
        try {
            String sql="select username,ip,port from users where status='online' and username=?";
            con = DriverManager.getConnection(url,name,passwords);
            ps=con.prepareStatement(sql);
            ps.setString(1,(String)cb.getSelectedItem());
            System.out.println((String)cb.getSelectedItem());
            rs=ps.executeQuery();
//            ta.append("我说："+tf.getText()+"\n");
            if(rs.next()){
                System.out.println("条件1");
                String usernames=rs.getString("username");
                    String ip=rs.getString("ip");
                    int post=rs.getInt("port");
                    byte [] ipB=new byte[4];
                    String ips[]=ip.split("\\.");
                    for (int i=0;i<ips.length;i++){
                        ipB[i]=(byte) Integer.parseInt(ips[i]);
                    }
                    String message=username+"悄悄的对你说了："+tf.getText();
                    byte [] m =message.getBytes();
                    DatagramPacket datagramPacket=new DatagramPacket(m,m.length);
                    datagramPacket.setAddress(InetAddress.getByAddress(ipB));
                    datagramPacket.setPort(post);
                    DatagramSocket datagramSocket=new DatagramSocket();
                    datagramSocket.send(datagramPacket);
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
    public void showinfoQun(){//广播
        System.out.println("jinru guangb");
        String url="jdbc:oracle:thin:@localhost:1521:orcl";
        String name="root";
        String passwords="1234";
        Connection con= null;
        ResultSet rs=null;
        PreparedStatement ps=null;
        try {
            /**
             * 查询所有在线状态的用户
             * 除了自身以为进行广播
             * **/
            String sql="select username,ip,port from users where status='online'";
            con = DriverManager.getConnection(url,name,passwords);
            ps=con.prepareStatement(sql);
            rs=ps.executeQuery();
            ta.append("我说："+tf.getText()+"\n");

            while(rs.next()){
                String usernames=rs.getString("username");
                if(!usernames.equals(username)){
                    /**
                     * TotalThread这里的线程主要是为了刷新登录者的栏目信息
                     * */
                    String ip=rs.getString("ip");
                    int post=rs.getInt("port");
                    byte [] ipB=new byte[4];
                    String ips[]=ip.split("\\.");
                    for (int i=0;i<ips.length;i++){
                        ipB[i]=(byte) Integer.parseInt(ips[i]);
                    }
                    String message=username+"说了："+tf.getText();
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
    public void showXXXInChatRoom(){//广播
        String url="jdbc:oracle:thin:@localhost:1521:orcl";
        String name="root";
        String passwords="1234";
        Connection con= null;
        ResultSet rs=null;
        PreparedStatement ps=null;
        try {
            /**
             * 查询所有在线状态的用户
             * 除了自身以为进行广播
             * **/
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
                    String message=usernames+"正在聊天室";
                     ta.append(message+'\n');
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void showXXXIntoChatRppm(){//广播
        String url="jdbc:oracle:thin:@localhost:1521:orcl";
        String name="root";
        String passwords="1234";
        Connection con= null;
        ResultSet rs=null;
        PreparedStatement ps=null;
        try {
            /**
             * 查询所有在线状态的用户
             * 除了自身以为进行广播
             * **/
            String sql="select username,ip,port from users where status='online'";
            con = DriverManager.getConnection(url,name,passwords);
            ps=con.prepareStatement(sql);
            rs=ps.executeQuery();
            while(rs.next()){
                String usernames=rs.getString("username");

                if(!usernames.equals(username)){
                    /**
                     * TotalThread这里的线程主要是为了刷新登录者的栏目信息
                     * */
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