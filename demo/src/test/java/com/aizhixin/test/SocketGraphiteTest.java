package com.aizhixin.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class SocketGraphiteTest {
    public static void main(String[] args) throws IOException {
        Random r = new Random();
        for (int i = 0; i < 200; i++) {
            PrintWriter write = null;
            Socket socket = null;
            try {
                socket = new Socket("172.16.23.110", 2003);
                write = new PrintWriter(socket.getOutputStream());
                long t = System.currentTimeMillis()/1000;
                int ri = r.nextInt(20);
                write.println("pz.test.mtrics " + ri + " " + t);
                write.flush();
                write.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != write) {
                    write.close();
                }
                if (null != socket) {
                    socket.close();
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
