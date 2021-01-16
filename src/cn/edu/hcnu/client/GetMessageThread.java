package cn.edu.hcnu.client;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class GetMessageThread extends Thread {
    private DatagramSocket datagramSocket = null;
    private DatagramPacket datagramPacke=null;
 private JTextArea ta;
 private   JComboBox cb;
    JFrame f;
    String username1;
    public GetMessageThread(ChatThreadWindow ch) {
        this.datagramSocket =ch.ds;
        this.cb=ch.cb;
        this.ta=ch.ta;
        this.f=ch.f;
        this.username1=ch.username;
    }
    /**
     * 该线程的功能是进行接收消息
     * */
    public void run() {
        try {
            while (true) {
                byte buff[] = new byte[1024];
                datagramPacke= new DatagramPacket(buff, buff.length);
                datagramSocket.receive(datagramPacke);
                String message=new String(buff);
                ta.append(message);
                ta.append("\n");
                System.out.println(message);
                if(message.contains("进入聊天室")){
                    message=message.replace("进入聊天室","");
                    cb.addItem(message);
                    /**
                     * TotalThread这里的线程主要是为了之前登录者的的栏目信息
                     * */
                    TotalThread totalThread=new TotalThread(f,username1);
                    totalThread.start();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

