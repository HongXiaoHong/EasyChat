package com.hong.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class NetWork {

    MsgCallBack msg;
    // 每个用户对应一个socket
    Map<String, Socket> userMap = Collections.synchronizedMap(
            new HashMap<>());

    public NetWork(MsgCallBack msg) {
        this.msg = msg;
    }

    // 开始服务
    public void startServer(final int port) {
        ThreadPoolUtils.submit(() -> {
            ServerSocket server = null;
            try {
                server = new ServerSocket(port);
                msg.onCreateServer(true);

                while (true) {// 监控链接

                    Socket socket = server.accept();
                    String ip = socket.getInetAddress().getHostAddress();
                    System.out.println("[NETWORK] IP地址：" + ip);
                    msg.onAccepted();
                    proccess(socket);
                }


            } catch (IOException e) {

                msg.onCreateServer(false);
                e.printStackTrace();
                System.out.println("�ڷ������е������������յ����쳣��Ϣ��" + e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        });
    }


    public String proccess(Socket socket) {
        String socketId = null;
        try {

            socketId = String.valueOf((int) (Math.random() * 10000));

            System.out.println("��network��  socketId = " + socketId);
            userMap.put(socketId, socket);
            read(socketId);
            return socketId;
        } catch (Exception e) {
            throw new RuntimeException(socketId);
        }

    }

    boolean read(final String socketId) {
        ThreadPoolUtils.submit(() -> {
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
                msg.onReveiveFailed(socketId);
            }
        });
        return false;
    }

    public void send(String socketId, String content) {
        System.out.println("content" + content);
        Socket socket = userMap.get(socketId);
        if (socket != null) {
            try {
                OutputStream out = socket.getOutputStream();
                out.write(content.getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("[NETWORK] 获取不到socket 无合适的socket可用");
        }
    }

    public InetSocketAddress getAddress(String socketId) {
        Socket socket = userMap.get(socketId);
        InetSocketAddress isa = null;
        if (socket != null) {
            isa = new InetSocketAddress(
                    socket.getInetAddress(), socket.getPort());
        }
        return isa;
    }
}
