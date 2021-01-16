package cn.edu.hcnu.Test;

import cn.edu.hcnu.client.LoginThread;

import java.net.Socket;

public class threadTest {
    public static void main(String[] args) {
        Thread login=new LoginThread();
        login.start();
    }
}
