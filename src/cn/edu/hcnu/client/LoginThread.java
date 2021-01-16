package cn.edu.hcnu.client;


import cn.edu.hcnu.util.MD5;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * 登录线程
 */
public class LoginThread extends Thread {
    private JFrame loginf;

    private JTextField t;

    public void run() {
        /*
         * 设置登录界面
         */
        loginf = new JFrame();
        loginf.setResizable(false);
        loginf.setLocation(300, 200);
        loginf.setSize(400, 150);
        loginf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginf.setTitle("聊天室" + " - 登录");

        t = new JTextField("Version " + "1.1.0" + "        By liwei");
        t.setHorizontalAlignment(JTextField.CENTER);
        t.setEditable(false);
        loginf.getContentPane().add(t, BorderLayout.SOUTH);

        JPanel loginp = new JPanel(new GridLayout(3, 2));
        loginf.getContentPane().add(loginp);

        JTextField t1 = new JTextField("登录名:");
        t1.setHorizontalAlignment(JTextField.CENTER);
        t1.setEditable(false);
        loginp.add(t1);

        final JTextField loginname = new JTextField("hqf");
        loginname.setHorizontalAlignment(JTextField.CENTER);
        loginp.add(loginname);

        JTextField t2 = new JTextField("密码:");
        t2.setHorizontalAlignment(JTextField.CENTER);
        t2.setEditable(false);
        loginp.add(t2);

        final JTextField loginPassword = new JTextField("hqf1234");
        loginPassword.setHorizontalAlignment(JTextField.CENTER);
        loginp.add(loginPassword);
        /*
         * 监听退出按钮(匿名内部类)
         */
        JButton b1 = new JButton("退  出");
        loginp.add(b1);
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        final JButton b2 = new JButton("登  录");
        loginp.add(b2);

        loginf.setVisible(true);

        /**
         * 监听器,监听"登录"Button的点击和TextField的回车
         */
        class ButtonListener implements ActionListener {
            private Socket s;

            public void actionPerformed(ActionEvent e) {
                String username=loginname.getText();
                String password=loginPassword.getText();
                Connection con= null;
                ResultSet rs=null;
                PreparedStatement ps=null;
                try {
                    String url="jdbc:oracle:thin:@localhost:1521:orcl";
                    String name="root";
                    String passwords="1234";
                    String sql="select password from users where username=?";
                    con = DriverManager.getConnection(url,name,passwords);
                    ps=con.prepareStatement(sql);
                    ps.setString(1,username);
                    rs=ps.executeQuery();
                if(rs.next()){
                    String pas=rs.getString("password");
                    if (MD5.checkpassword(password,pas)){
                        System.out.println("登录成功");
                        loginf.setVisible(false);
                        InetAddress inetAddress=InetAddress.getLocalHost();/**获取登录者的电脑的IP地址**/
                        String IP=inetAddress.getHostAddress();
                        System.out.println(IP);
                        int bort=8888;
                        DatagramSocket datagramSocket=null;
                        /**以下while是处理端口占用**/
                        while(true){
                            try {
                                datagramSocket =new DatagramSocket(bort);
                                break;
                            } catch (IOException ex) {
                                bort+=1;
//                                ex.printStackTrace();
                            }
                        }
                        /**进行登录者数据的更新**/
                        String sql1="update users set ip=?,port=?,status=? where username=?";
                        con = DriverManager.getConnection(url,name,passwords);
                        ps=con.prepareStatement(sql1);
                        ps.setString(1,IP);
                        ps.setInt(2,bort);
                        ps.setString(3,"online");
                        ps.setString(4,username);
                        ps.executeUpdate();
                        ChatThreadWindow chatThreadWindow1=new ChatThreadWindow(username,datagramSocket);
                    }else{
                        System.out.println("登录失败");
                    }
                }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (NoSuchAlgorithmException ex) {
                    ex.printStackTrace();
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                }

				/*
				1、根据用户去数据库把加密后的密码拿到
				SELECT password FROM users WHERE username='liwei';
				2、把登录界面输入的密码和数据库里加密后的进行比对（调用MD5类的checkpassword方法）
				 */
            }
        }
        ButtonListener bl = new ButtonListener();
        b2.addActionListener(bl);
        loginname.addActionListener(bl);
        loginPassword.addActionListener(bl);
    }
}