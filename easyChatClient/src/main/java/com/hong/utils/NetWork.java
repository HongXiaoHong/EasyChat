package com.hong.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class NetWork {

    MsgCallBack msg;
    //使用collections的同步方法将集合进行同步
    Map<String, Socket> userMap = Collections.synchronizedMap(
            new HashMap<String, Socket>());

    public NetWork(MsgCallBack msg) {
        this.msg = msg;
    }

    public String startConnect(final String ip, final int port) {

        Callable<String> task = () -> {
            String socketId = null;
            Socket socket = null;
            try {
                socket = new Socket(ip, port);
                msg.onConntextEvent(true);
                socketId = proccess(socket);
            } catch (IOException e) {
                msg.onConntextEvent(false);
                e.printStackTrace();
            } finally {

            }

            return socketId;
        };

        FutureTask<String> ft = new FutureTask<String>(task);
        Thread th = new Thread(ft);
        th.start();

        String socketId = null;
        try {
            socketId = ft.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return socketId;
    }

    public String proccess(Socket socket) {
        String socketId = String.valueOf((int) (Math.random() * 10000));

        System.out.println("【易聊客户端network】  socketId = " + socketId);
        userMap.put(socketId, socket);
        read(socketId);
        return socketId;
    }

    boolean read(final String socketId) {
        (new Thread(() -> {
            int count = 0;
            byte[] buffer = new byte[1024];
            try {
                Socket socket = userMap.get(socketId);
                InputStream is = socket.getInputStream();
                while (true) {
                    count = is.read(buffer);
                    if (count > 0) {
                        msg.onReceived(buffer, count, socketId);
                    }
                }
            } catch (IOException e) {
                msg.onReveiveFailed();
                e.printStackTrace();
            }
        })).start();

        return false;
    }

    public void send(String socketId, String content) {
        Socket socket = userMap.get(socketId);
        if (socket != null) {
            try {
                OutputStream out = socket.getOutputStream();
                out.write(content.getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("【易聊客户端NETWORK】找不到相应的socket");
        }
    }

    public MsgCallBack callBack() {
        return msg;
    }

    public void copySocket(String oldId, String newId) {
        Socket socket = userMap.get(oldId);
        if (socket != null) {
            userMap.put(newId, socket);
        }
    }
}
